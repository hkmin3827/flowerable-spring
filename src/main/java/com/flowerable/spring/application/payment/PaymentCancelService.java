package com.flowerable.spring.application.payment;

import com.flowerable.spring.domain.order.constant.OrderCancelReason;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.payment.Payment;
import com.flowerable.spring.domain.payment.PaymentRepository;
import com.flowerable.spring.domain.payment.PaymentStatus;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    @Value("${toss.api.timeout}")
    private long cancelTimeoutSeconds;

    private final WebClient tossClient;

    private final PaymentRepository paymentRepository;

    public void cancelPayment(OrderRequest order, OrderCancelReason cancelReason) {
        Payment payment = paymentRepository
                .findTopByOrderIdAndStatusOrderByCreatedAtDesc(order.getId(), PaymentStatus.DONE)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        callTossCancel(payment.getPaymentKey(), cancelReason.getDescription());
        payment.markCanceled(cancelReason);
    }

    public void cancelTossDirectly(String paymentKey, String cancelReason) {
        callTossCancel(paymentKey, cancelReason);
    }

    private void callTossCancel(String paymentKey, String cancelReason) {
        try {
            tossClient.post()
                    .uri("/v1/payments/" + paymentKey + "/cancel")
                    .bodyValue(Map.of("cancelReason", cancelReason))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(cancelTimeoutSeconds))
                    .onErrorMap(TimeoutException.class, e -> {
                        log.error("[PaymentCancel] Toss cancel API 타임아웃 ({}초 초과). paymentKey={}",
                                cancelTimeoutSeconds, paymentKey);
                        return new CustomException(ErrorCode.PAYMENT_TOSS_TIMEOUT);
                    })
                    .block();

        } catch (WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            log.warn("[PaymentCancel] Toss 취소 응답. status={}, body={}", e.getStatusCode(), body);

            if (body.contains("ALREADY_CANCELED_PAYMENT")) {
                return;
            }

            throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }
    }
}