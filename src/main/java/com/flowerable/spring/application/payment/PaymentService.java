package com.flowerable.spring.application.payment;

import com.flowerable.spring.domain.payment.Payment;
import com.flowerable.spring.domain.payment.PaymentRepository;
import com.flowerable.spring.domain.payment.PaymentStatus;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.notification.NotificationReceiverType;
import com.flowerable.spring.domain.notification.NotificationType;
import com.flowerable.spring.domain.order.constant.OrderCancelReason;
import com.flowerable.spring.application.notification.dto.NotificationCreateReq;
import com.flowerable.spring.application.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.application.notification.NotificationService;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${toss.secret-key}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final OrderRequestRepository orderRequestRepository;
    private final NotificationService notificationService;
    private final WebClient.Builder webClientBuilder;


    @Transactional
    public Long confirm(PaymentConfirmReq req) {

        OrderRequest order = orderRequestRepository.findByOrderNumber(req.getOrderId())
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getTotalPrice().equals(req.getAmount())) {
            throw new RuntimeException("금액 불일치");
        }

        if (req.getPaymentKey() == null)
            throw new CustomException(ErrorCode.PAYMENT_KEY_NOT_FILLED);

        if(order.isPaid()) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_PAID);
        }

        Payment payment = Payment.createReady(order, req.getAmount());
        paymentRepository.save(payment);

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl("https://api.tosspayments.com")
                    .defaultHeader(HttpHeaders.AUTHORIZATION,
                            "Basic " + Base64.getEncoder()
                                    .encodeToString((secretKey + ":").getBytes()))
                    .build();

            webClient.post()
                    .uri("/v1/payments/confirm")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            payment.markDone(req.getPaymentKey());

            order.markRequested();

            String content = order.getMessage() == null
                    ? "주문 확인 후 접수 또는 취소해주세요."
                    : "주문 확인 후 접수 또는 취소해주세요. (요청 사항 : " + order.getMessage()+ ")";
            notifyShop(order, order.getShop().getId(), NotificationType.ORDER_CREATED, content);

            return order.getId();
        } catch(WebClientResponseException e) {

            String body = e.getResponseBodyAsString();

            if (body.contains("ALREADY_PROCESSED_PAYMENT")) {
                payment.markDone(req.getPaymentKey());
                order.markRequested();
                return order.getId();
            }

            saveFailed(payment, e.getMessage());

            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailed(Payment payment, String reason) {
        payment.markFailed(reason);
    }

    public void cancelPayment(OrderRequest order, OrderCancelReason cancelReason) {

        Payment payment = paymentRepository
                .findTopByOrderIdAndStatusOrderByCreatedAtDesc(
                        order.getId(),
                        PaymentStatus.DONE
                )
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        try {

            WebClient webClient = WebClient.builder()
                    .baseUrl("https://api.tosspayments.com")
                    .defaultHeader(org.springframework.http.HttpHeaders.AUTHORIZATION,
                            "Basic " + Base64.getEncoder()
                                    .encodeToString((secretKey + ":").getBytes()))
                    .build();

            webClient.post()
                    .uri("/v1/payments/" + payment.getPaymentKey() + "/cancel")
                    .bodyValue(Map.of("cancelReason", cancelReason.getDescription()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            payment.markCanceled(cancelReason);

        } catch (WebClientResponseException e) {

            if (e.getResponseBodyAsString().contains("ALREADY_CANCELED_PAYMENT")) {
                payment.markCanceled(cancelReason);
                return;
            }
            // Toss 취소 실패 → 롤백 유도
            System.out.println("status = " + e.getStatusCode());
            System.out.println("body = " + e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);        }
    }

    private void notifyShop(OrderRequest order, Long receiverId, NotificationType type, String content) {
        notificationService.createNotification(
                new NotificationCreateReq(
                        NotificationReceiverType.SHOP,
                        receiverId,
                        type,
                        order.getOrderNumber() + " : " + type.getTitle(),
                        content,
                        order.getId()
                )
        );
    }
}