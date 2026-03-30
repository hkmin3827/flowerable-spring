package com.flowerable.spring.application.payment;

import com.flowerable.spring.application.notification.NotificationService;
import com.flowerable.spring.application.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.domain.notification.NotificationType;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.payment.Payment;
import com.flowerable.spring.domain.payment.PaymentRepository;
import com.flowerable.spring.domain.payment.PaymentStatus;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentWriterTest {

    @InjectMocks
    private PaymentWriter paymentWriter;

    @Mock
    private OrderRequestRepository orderRequestRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("결제 확정 저장 - Payment DONE 생성, Order REQUESTED 전환, 알림 발송")
    void persistConfirmedPayment_success() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");
        OrderRequest order = buildOrderWithMessage("ORD-001", 100L, false, null);

        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        Long result = paymentWriter.persistConfirmedPayment(req);

        assertThat(result).isEqualTo(100L);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment saved = paymentCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(saved.getAmount()).isEqualTo(11000);

        verify(notificationService).notifyShopOrderRequested(
                eq(order), eq(20L), eq(NotificationType.ORDER_REQUESTED), anyString());
    }

    @Test
    @DisplayName("결제 확정 - 이미 결제된 주문(isPaid=true)이면 중복 저장 없이 orderId 반환")
    void persistConfirmedPayment_alreadyPaid_skipsAndReturnsId() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");
        OrderRequest order = buildOrderWithMessage("ORD-001", 100L, true, null);

        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        Long result = paymentWriter.persistConfirmedPayment(req);

        assertThat(result).isEqualTo(100L);
        verify(paymentRepository, never()).save(any());
        verify(notificationService, never()).notifyShopOrderRequested(any(), any(), any(), any());
    }

    @Test
    @DisplayName("결제 확정 - 주문 없으면 OrderNotFoundException")
    void persistConfirmedPayment_orderNotFound_throws() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-999", "pay_key_123");

        given(orderRequestRepository.findByOrderNumber("ORD-999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentWriter.persistConfirmedPayment(req))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("결제 확정 - 요청 사항 있으면 알림 메시지에 포함")
    void persistConfirmedPayment_withMessage_includesInNotification() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");
        OrderRequest order = buildOrderWithMessage("ORD-001", 100L, false, "문 앞에 놔주세요");

        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        paymentWriter.persistConfirmedPayment(req);

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService).notifyShopOrderRequested(any(), any(), any(), contentCaptor.capture());
        assertThat(contentCaptor.getValue()).contains("문 앞에 놔주세요");
    }

    @Test
    @DisplayName("결제 실패 기록 - Payment FAILED 상태로 저장, failReason 포함")
    void persistFailedPayment_success() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");
        OrderRequest order = buildOrderWithMessage("ORD-001", 100L, false, null);
        String failReason = "토스 결제 실패, 주문 목록에서 결제를 재시도 해주세요.";

        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        paymentWriter.persistFailedPayment(req, failReason);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(saved.getFailReason()).isEqualTo(failReason);
    }

    @Test
    @DisplayName("결제 실패 기록 - 주문 없으면 OrderNotFoundException")
    void persistFailedPayment_orderNotFound_throws() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-999", "pay_key_123");

        given(orderRequestRepository.findByOrderNumber("ORD-999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentWriter.persistFailedPayment(req, "실패"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private OrderRequest buildOrderWithMessage(String orderNumber, Long id, boolean isPaid, String message) {
        Shop shop = mock(Shop.class);
        lenient().when(shop.getId()).thenReturn(20L);

        OrderRequest order = mock(OrderRequest.class);
        lenient().when(order.getId()).thenReturn(id);
        lenient().when(order.getOrderNumber()).thenReturn(orderNumber);
        lenient().when(order.isPaid()).thenReturn(isPaid);
        lenient().when(order.getShop()).thenReturn(shop);
        if (message != null) {
            lenient().when(order.getMessage()).thenReturn(message);
        }
        return order;
    }
}