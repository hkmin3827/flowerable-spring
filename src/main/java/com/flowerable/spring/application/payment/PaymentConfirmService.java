package com.flowerable.spring.application.payment;

import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.application.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConfirmService {

    private static final String LOCK_KEY_PREFIX = "lock:payment-confirm:";
    private static final long LOCK_WAIT_SECONDS  = 5;
    private static final long LOCK_LEASE_SECONDS = 30;

    @Value("${toss.api.timeout}")
    private long confirmTimeoutSeconds;

    @Value("${tosspayment.idempotency.expiration}")
    private long idempotencyExpirationMillis;

    private final OrderRequestRepository orderRequestRepository;
    private final WebClient tossWebClient;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final PaymentWriter paymentWriter;
    private final PaymentCancelService paymentCancelService;


    public Long processPay(PaymentConfirmReq req) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + req.getOrderId());

        try {
            if (!lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                throw new CustomException(ErrorCode.PAYMENT_CONCURRENT_REQUEST);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.PAYMENT_CONCURRENT_REQUEST);
        }

        try {
            return confirm(req);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private Long confirm(PaymentConfirmReq req) {
        validateBeforePayment(req);

        String idempotencyKey = resolveIdempotencyKey(req);

        try {
            callTossConfirm(req, idempotencyKey);
        } catch (WebClientResponseException e) {
            return handleTossError(req, e);
        }

        try {
            return paymentWriter.persistConfirmedPayment(req);
        } catch (Exception internalEx) {
            log.error("[Payment] Toss 승인 후 DB 저장 실패 → 보상 취소 진행 --> paymentKey={}", req.getPaymentKey(), internalEx);
            tryCancelAboutInternalError(req.getPaymentKey());
            throw new CustomException(ErrorCode.PAYMENT_SAVE_FAILED);
        }
    }

    private void validateBeforePayment(PaymentConfirmReq req) {
        if (req.getPaymentKey() == null) {
            throw new CustomException(ErrorCode.PAYMENT_KEY_NOT_FILLED);
        }

        OrderRequest order = orderRequestRepository.findByOrderNumber(req.getOrderId())
                .orElseThrow(OrderNotFoundException::new);

        if (order.isPaid()) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_DONE);
        }

        if (!order.getTotalPrice().equals(req.getAmount())) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_BAD_REQUEST);
        }
    }

    private String resolveIdempotencyKey(PaymentConfirmReq req) {
        String redisKey = "idempotency-key:" + req.getPaymentKey();
        String cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            return cached;
        }
        String newKey = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(redisKey, newKey, idempotencyExpirationMillis, TimeUnit.MILLISECONDS);
        return newKey;
    }

    private void callTossConfirm(PaymentConfirmReq req, String idempotencyKey) {
        tossWebClient.mutate()
                .defaultHeader("Idempotency-Key", idempotencyKey)
                .build()
                .post()
                .uri("/v1/payments/confirm")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(confirmTimeoutSeconds))
                .onErrorMap(TimeoutException.class, e -> {
                    log.error("[Payment] Toss confirm API 타임아웃 ({}초 초과). orderId={}",
                            confirmTimeoutSeconds, req.getOrderId());
                    return WebClientResponseException.create(
                            408,
                            "Request Timeout",
                            null, null, null
                    );
                })
                .block();
    }

    private Long handleTossError(PaymentConfirmReq req, WebClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        int code   = ex.getStatusCode().value();
        log.warn("[Payment] Toss non-200 에러. status = {}, body = {}", code, body);

        if (code == 408) {
            throw new CustomException(ErrorCode.PAYMENT_TOSS_TIMEOUT);
        }

        if (body.contains("ALREADY_PROCESSED_PAYMENT")) {
            try {
                return paymentWriter.persistConfirmedPayment(req);
            } catch (Exception e) {
                log.warn("[Payment] ALREADY_PROCESSED 재저장 스킵. paymentKey={}", req.getPaymentKey());
                return orderRequestRepository.findByOrderNumber(req.getOrderId())
                        .orElseThrow(OrderNotFoundException::new).getId();
            }
        }
        if (code == 409 && body.contains("IDEMPOTENT_REQUEST_PROCESSING")) {
            throw new CustomException(ErrorCode.PAYMENT_CONCURRENT_REQUEST);
        }

        try {
            paymentWriter.persistFailedPayment(req, "토스 결제 실패, 주문 목록에서 결제를 재시도 해주세요.");
        } catch (Exception e) {
            log.warn("[Payment] FAILED 기록 저장 실패. orderId = {}", req.getOrderId(), ex);
        }
        throw ex;
    }

    private void tryCancelAboutInternalError(String paymentKey) {
        try {
            paymentCancelService.cancelTossDirectly(paymentKey, "시스템 오류로 인한 자동 취소");
            log.info("[Payment] 서버 오류로 인한 토스 결제 롤백 : paymentKey = {}", paymentKey);
        } catch (Exception e) {
            log.error("[Payment] !!!! 보상 취소 실패 !!!! - 관리자 확인 후 주문 requested 처리와 같은 후처리가 필요합니다. paymentKey = {}", paymentKey, e);
        }
    }
}