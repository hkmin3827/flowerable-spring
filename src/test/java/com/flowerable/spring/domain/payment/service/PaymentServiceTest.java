package com.flowerable.spring.domain.payment.service;

import com.flowerable.spring.domain.notification.service.NotificationService;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.domain.payment.entity.Payment;
import com.flowerable.spring.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRequestRepository orderRequestRepository;

    @Mock
    private NotificationService notificationService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient.Builder webClientBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "secretKey", "test_secret_key");

        given(webClientBuilder.baseUrl(anyString())).willReturn(webClientBuilder);
        given(webClientBuilder.defaultHeader(anyString(), anyString())).willReturn(webClientBuilder);
        given(webClientBuilder.build()).willReturn(webClient);
    }

    @Test
    @DisplayName("토스 결제 승인 성공 시 - 결제 기록 저장 및 주문 상태 변경")
    void confirmPayment_success() {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-123", "paymentKey123");

        com.flowerable.spring.domain.shop.entity.Shop mockShop = mock(com.flowerable.spring.domain.shop.entity.Shop.class);
        given(mockShop.getId()).willReturn(20L);

        OrderRequest order = OrderRequest.create("ORD-123", null, mockShop, "WHITE", 1000, List.of(), "요청");
        ReflectionTestUtils.setField(order, "totalPrice", 11000);
        ReflectionTestUtils.setField(order, "id", 100L);

        given(orderRequestRepository.findByOrderNumber("ORD-123")).willReturn(Optional.of(order));

        given(webClient.post()
                .uri(anyString())
                .bodyValue(any())
                .retrieve()
                .bodyToMono(String.class)
                .block()
        ).willReturn("{\"status\":\"DONE\", \"totalAmount\":11000}");

        paymentService.confirm(req);

        verify(paymentRepository).save(any(Payment.class));
        verify(notificationService).createNotification(any());
    }

    @Test
    @DisplayName("결제 금액 위변조 검증 - DB 주문 금액과 요청 금액이 다르면 예외 발생")
    void confirmPayment_amountMismatch_throwsException() {
        PaymentConfirmReq req = new PaymentConfirmReq(1000, "ORD-123", "paymentKey123");

        OrderRequest order = OrderRequest.create("ORD-123", null, null, "WHITE", 1000, List.of(), "요청");
        ReflectionTestUtils.setField(order, "totalPrice", 11000);

        given(orderRequestRepository.findByOrderNumber("ORD-123")).willReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.confirm(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("금액 불일치");
    }
}