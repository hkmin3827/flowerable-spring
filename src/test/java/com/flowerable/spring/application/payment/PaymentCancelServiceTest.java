package com.flowerable.spring.application.payment;

import com.flowerable.spring.domain.order.constant.OrderCancelReason;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.payment.Payment;
import com.flowerable.spring.domain.payment.PaymentRepository;
import com.flowerable.spring.domain.payment.PaymentStatus;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCancelServiceTest {

    @InjectMocks
    private PaymentCancelService paymentCancelService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient tossClient;

    @Test
    @DisplayName("정상 취소 - Toss 성공 후 Payment CANCELED 마킹")
    void cancelPayment_success() {
        OrderRequest order = mockOrder(1L);
        Payment payment = mockPayment("pay_key_done");

        given(paymentRepository.findTopByOrderIdAndStatusOrderByCreatedAtDesc(1L, PaymentStatus.DONE))
                .willReturn(Optional.of(payment));
        stubTossCancel_success("pay_key_done");

        paymentCancelService.cancelPayment(order, OrderCancelReason.CUSTOMER_REQUEST);

        verify(payment).markCanceled(OrderCancelReason.CUSTOMER_REQUEST);
    }

    @Test
    @DisplayName("DONE Payment 없으면 PAYMENT_NOT_FOUND 예외 - Toss 미호출")
    void cancelPayment_noDonePayment_throwsNotFound() {
        OrderRequest order = mockOrder(1L);

        given(paymentRepository.findTopByOrderIdAndStatusOrderByCreatedAtDesc(1L, PaymentStatus.DONE))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCancelService.cancelPayment(order, OrderCancelReason.CUSTOMER_REQUEST))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);

        verifyNoInteractions(tossClient);
    }

    @Test
    @DisplayName("Toss 취소 실패 - PAYMENT_CANCEL_FAILED 예외, markCanceled 미호출")
    void cancelPayment_tossFails_throwsCancelFailed() {
        OrderRequest order = mockOrder(1L);
        Payment payment = mockPayment("pay_key_done");

        given(paymentRepository.findTopByOrderIdAndStatusOrderByCreatedAtDesc(1L, PaymentStatus.DONE))
                .willReturn(Optional.of(payment));
        stubTossCancel_error("pay_key_done", "SOME_ERROR", 500);

        assertThatThrownBy(() -> paymentCancelService.cancelPayment(order, OrderCancelReason.CUSTOMER_REQUEST))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_CANCEL_FAILED);

        verify(payment, never()).markCanceled(any());
    }

    @Test
    @DisplayName("Toss가 ALREADY_CANCELED_PAYMENT 반환 - 정상 처리(멱등), markCanceled 호출")
    void cancelPayment_alreadyCanceled_treatedAsSuccess() {
        OrderRequest order = mockOrder(1L);
        Payment payment = mockPayment("pay_key_done");

        given(paymentRepository.findTopByOrderIdAndStatusOrderByCreatedAtDesc(1L, PaymentStatus.DONE))
                .willReturn(Optional.of(payment));
        stubTossCancel_error("pay_key_done", "ALREADY_CANCELED_PAYMENT", 200);

        paymentCancelService.cancelPayment(order, OrderCancelReason.CUSTOMER_REQUEST);

        verify(payment).markCanceled(OrderCancelReason.CUSTOMER_REQUEST);
    }

    @Test
    @DisplayName("cancelTossDirectly - paymentRepository 조회 없이 Toss 취소 API만 호출")
    void cancelTossDirectly_doesNotQueryRepository() {
        stubTossCancel_success("pay_key_direct");

        paymentCancelService.cancelTossDirectly("pay_key_direct", "시스템 오류로 인한 자동 취소");

        verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("cancelTossDirectly - Toss 성공 시 예외 없이 정상 종료")
    void cancelTossDirectly_success_noException() {
        stubTossCancel_success("pay_key_direct");

        assertThatCode(() -> paymentCancelService.cancelTossDirectly("pay_key_direct", "자동 취소"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("cancelTossDirectly - Toss 실패 시 PAYMENT_CANCEL_FAILED 예외")
    void cancelTossDirectly_tossFails_throwsCancelFailed() {
        stubTossCancel_error("pay_key_direct", "SOME_ERROR", 500);

        assertThatThrownBy(() -> paymentCancelService.cancelTossDirectly("pay_key_direct", "자동 취소"))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_CANCEL_FAILED);
    }

    @Test
    @DisplayName("cancelTossDirectly - ALREADY_CANCELED_PAYMENT 멱등 처리, 예외 없음")
    void cancelTossDirectly_alreadyCanceled_noException() {
        stubTossCancel_error("pay_key_direct", "ALREADY_CANCELED_PAYMENT", 200);

        assertThatCode(() -> paymentCancelService.cancelTossDirectly("pay_key_direct", "자동 취소"))
                .doesNotThrowAnyException();
    }

    private OrderRequest mockOrder(Long id) {
        OrderRequest order = mock(OrderRequest.class);
        lenient().when(order.getId()).thenReturn(id);
        return order;
    }

    private Payment mockPayment(String paymentKey) {
        Payment payment = mock(Payment.class);
        lenient().when(payment.getPaymentKey()).thenReturn(paymentKey);
        return payment;
    }

    private void stubTossCancel_success(String paymentKey) {
        given(tossClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .bodyValue(anyMap())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(any(Duration.class))
                .onErrorMap(eq(TimeoutException.class), any())
                .block())
                .willReturn("{\"status\":\"CANCELED\"}");
    }

    private void stubTossCancel_error(String paymentKey, String errorCode, int httpStatus) {
        WebClientResponseException ex = mock(WebClientResponseException.class);
        lenient().when(ex.getResponseBodyAsString()).thenReturn("{\"code\":\"" + errorCode + "\"}");
        lenient().when(ex.getStatusCode()).thenReturn(HttpStatus.valueOf(httpStatus));

        given(tossClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .bodyValue(anyMap())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(any(Duration.class))
                .onErrorMap(eq(TimeoutException.class), any())
                .block())
                .willThrow(ex);
    }
}