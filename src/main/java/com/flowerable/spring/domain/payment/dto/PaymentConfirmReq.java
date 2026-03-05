package com.flowerable.spring.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentConfirmReq {
    private Integer amount;
    private String orderId;   // 주문번호 = orderNumber
    private String paymentKey;
}
