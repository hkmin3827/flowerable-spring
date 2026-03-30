package com.flowerable.spring.application.payment;

import com.flowerable.spring.application.notification.NotificationService;
import com.flowerable.spring.application.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.domain.notification.NotificationType;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.payment.Payment;
import com.flowerable.spring.domain.payment.PaymentRepository;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWriter {

    private final OrderRequestRepository orderRequestRepository;
    private final PaymentRepository      paymentRepository;
    private final NotificationService    notificationService;

    @Transactional
    public Long persistConfirmedPayment(PaymentConfirmReq req) {
        OrderRequest order = orderRequestRepository.findByOrderNumber(req.getOrderId())
                .orElseThrow(OrderNotFoundException::new);

        if (order.isPaid()) {
            log.info("[PaymentWriter] 이미 결제 완료된 주문 : orderId={}", order.getId());
            return order.getId();
        }

        Payment payment = Payment.createReady(order, req.getAmount(), req.getPaymentKey());
        payment.markDone();
        paymentRepository.save(payment);

        order.markRequested();

        notificationService.notifyShopOrderRequested(
                order,
                order.getShop().getId(),
                NotificationType.ORDER_REQUESTED,
                buildNotificationContent(order)
        );

        log.info("[PaymentWriter] 결제 확정 저장 완료. orderId={}, paymentId={}", order.getId(), payment.getId());
        return order.getId();
    }

    @Transactional
    public void persistFailedPayment(PaymentConfirmReq req, String failReason) {
        OrderRequest order = orderRequestRepository.findByOrderNumber(req.getOrderId())
                .orElseThrow(OrderNotFoundException::new);

        Payment payment = Payment.createReady(order, req.getAmount(), req.getPaymentKey());
        payment.markFailed(failReason);
        paymentRepository.save(payment);

        log.info("[PaymentWriter] 결제 실패 기록. orderId={}, reason={}", order.getId(), failReason);
    }

    private String buildNotificationContent(OrderRequest order) {
        if (order.getMessage() == null || order.getMessage().isBlank()) {
            return "주문 확인 후 접수 또는 취소해주세요.";
        }
        return "주문 확인 후 접수 또는 취소해주세요. (요청 사항 : " + order.getMessage() + ")";
    }
}