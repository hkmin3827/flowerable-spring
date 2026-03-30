package com.flowerable.spring.application.payment;

import com.flowerable.spring.application.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mock.Strictness.LENIENT;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentConfirmServiceTest {

    @InjectMocks
    private PaymentConfirmService paymentConfirmService;

    @Mock
    private OrderRequestRepository orderRequestRepository;

    @Mock(strictness = LENIENT)
    private PaymentWriter paymentWriter;

    @Mock
    private PaymentCancelService paymentCancelService;

    @Mock(strictness = LENIENT)
    private StringRedisTemplate redisTemplate;

    @Mock(strictness = LENIENT)
    private ValueOperations<String, String> valueOps;

    @Mock
    private RedissonClient redissonClient;

    @Mock(strictness = LENIENT)
    private RLock rLock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient tossClient;

    @BeforeEach
    void setUp() throws InterruptedException {
        ReflectionTestUtils.setField(paymentConfirmService, "idempotencyExpirationMillis", 86400000L);

        given(redissonClient.getLock(anyString())).willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        given(rLock.isHeldByCurrentThread()).willReturn(true);

        given(redisTemplate.opsForValue()).willReturn(valueOps);
        given(valueOps.get(anyString())).willReturn(null);
    }

    @Test
    @DisplayName("정상 결제 승인 - PaymentWriter 호출 및 orderId 반환")
    void confirm_success() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");

        OrderRequest order = mockOrder(false);
        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        stubTossSuccess();
        given(paymentWriter.persistConfirmedPayment(req)).willReturn(100L);

        Long result = paymentConfirmService.processPay(req);

        assertThat(result).isEqualTo(100L);
        verify(paymentWriter).persistConfirmedPayment(req);
        verify(paymentCancelService, never()).cancelTossDirectly(any(), any());
    }

    @Test
    @DisplayName("중복 요청 - 락 획득 실패 시 PAYMENT_CONCURRENT_REQUEST 예외")
    void confirm_lockFailed_throwsConcurrentException() throws InterruptedException {
        given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(false);

        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");

        assertThatThrownBy(() -> paymentConfirmService.processPay(req))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_CONCURRENT_REQUEST);
    }

    @Test
    @DisplayName("paymentKey 누락 - PAYMENT_KEY_NOT_FILLED 예외")
    void confirm_nullPaymentKey_throwsException() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", null);

        assertThatThrownBy(() -> paymentConfirmService.processPay(req))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_KEY_NOT_FILLED);
    }

    @Test
    @DisplayName("금액 위변조 - 주문 금액 불일치 시 PAYMENT_AMOUNT_BAD_REQUEST 예외")
    void confirm_amountMismatch_throwsException() {
        PaymentConfirmReq req = new PaymentConfirmReq(999, "ORD-001", "pay_key_123");

        OrderRequest order = mockOrder(false);
        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentConfirmService.processPay(req))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_AMOUNT_BAD_REQUEST);

        verify(paymentWriter, never()).persistConfirmedPayment(any());
    }

    @Test
    @DisplayName("이미 결제된 주문 - isPaid()==true 이면 PAYMENT_ALREADY_DONE 예외")
    void confirm_alreadyPaid_throwsException() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");

        OrderRequest order = mockOrder(true);
        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentConfirmService.processPay(req))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_ALREADY_DONE);
    }

    @Test
    @DisplayName("Toss 200 후 DB 실패 - 보상 취소 호출 후 PAYMENT_SAVE_FAILED 예외")
    void confirm_tossSuccessButDbFails_callsCompensatingCancel() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");

        OrderRequest order = mockOrder(false);
        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        stubTossSuccess();
        given(paymentWriter.persistConfirmedPayment(req)).willThrow(new RuntimeException("DB 저장 실패"));

        assertThatThrownBy(() -> paymentConfirmService.processPay(req))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_SAVE_FAILED);

        verify(paymentCancelService).cancelTossDirectly(eq("pay_key_123"), anyString());
    }

    @Test
    @DisplayName("Toss 일반 실패 - FAILED 기록 후 WebClientResponseException 재전파")
    void confirm_tossError_persistFailedAndRethrows() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");

        OrderRequest order = mockOrder(false);
        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        stubTossError("UNKNOWN_ERROR", 400);

        assertThatThrownBy(() -> paymentConfirmService.processPay(req))
                .isInstanceOf(WebClientResponseException.class);

        verify(paymentWriter).persistFailedPayment(eq(req), anyString());
        verify(paymentCancelService, never()).cancelTossDirectly(any(), any());
    }

    @Test
    @DisplayName("IDEMPOTENT_REQUEST_PROCESSING(409) - 재시도 없이 PAYMENT_CONCURRENT_REQUEST 예외")
    void confirm_idempotentProcessing_throwsConcurrentRequest() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");

        OrderRequest order = mockOrder(false);
        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        stubTossError("IDEMPOTENT_REQUEST_PROCESSING", 409);

        assertThatThrownBy(() -> paymentConfirmService.processPay(req))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_CONCURRENT_REQUEST);

        verify(paymentWriter, never()).persistFailedPayment(any(), any());
        verify(paymentCancelService, never()).cancelTossDirectly(any(), any());
    }

    @Test
    @DisplayName("ALREADY_PROCESSED_PAYMENT - persistConfirmedPayment 호출 후 orderId 반환")
    void confirm_alreadyProcessed_persistsConfirmedAndReturns() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-001", "pay_key_123");

        OrderRequest order = mockOrder(false);
        given(orderRequestRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order));

        stubTossError("ALREADY_PROCESSED_PAYMENT", 400);
        given(paymentWriter.persistConfirmedPayment(req)).willReturn(100L);

        Long result = paymentConfirmService.processPay(req);

        assertThat(result).isEqualTo(100L);
        verify(paymentWriter).persistConfirmedPayment(req);
    }

    private OrderRequest mockOrder(boolean isPaid) {
        OrderRequest order = mock(OrderRequest.class);
        lenient().when(order.getId()).thenReturn(100L);
        lenient().when(order.getOrderNumber()).thenReturn("ORD-001");
        lenient().when(order.getTotalPrice()).thenReturn(11000);
        lenient().when(order.isPaid()).thenReturn(isPaid);
        lenient().when(order.getShop()).thenReturn(mock(Shop.class));
        return order;
    }

    private void stubTossSuccess() {
        given(tossClient.mutate()
                .defaultHeader(anyString(), anyString())
                .build()
                .post()
                .uri(anyString())
                .bodyValue(any())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(any(Duration.class))
                .onErrorMap(eq(TimeoutException.class), any())
                .block())
                .willReturn("{\"status\":\"DONE\"}");
    }

    private void stubTossError(String errorCode, int httpStatus) {
        WebClientResponseException ex = mock(WebClientResponseException.class);
        lenient().when(ex.getResponseBodyAsString()).thenReturn("{\"code\":\"" + errorCode + "\"}");
        lenient().when(ex.getStatusCode()).thenReturn(HttpStatus.valueOf(httpStatus));

        given(tossClient.mutate()
                .defaultHeader(anyString(), anyString())
                .build()
                .post()
                .uri(anyString())
                .bodyValue(any())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(any(Duration.class))
                .onErrorMap(eq(TimeoutException.class), any())
                .block())
                .willThrow(ex);
    }
}