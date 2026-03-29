package com.flowerable.spring.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowerable.spring.application.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.application.payment.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PaymentController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.flowerable.spring.global.jwt.JwtAuthenticationFilter.class,
                                com.flowerable.spring.global.config.SecurityConfig.class
                        }
                )
        }
)
@Import(com.flowerable.spring.global.config.TestSecurityConfig.class)
@TestPropertySource(properties ={
        "spring.cloud.aws.parameterstore.enabled=false",
        "spring.config.import="
})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("POST /api/payments/confirm - 결제 승인 요청 성공 (200)")
    void confirmPayment_success_returns200() throws Exception {
        PaymentConfirmReq req = new PaymentConfirmReq(11000, "ORD-123", "paymentKey123");

        given(paymentService.confirm(any(PaymentConfirmReq.class))).willReturn(1L);

        mockMvc.perform(post("/api/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1));
    }
}