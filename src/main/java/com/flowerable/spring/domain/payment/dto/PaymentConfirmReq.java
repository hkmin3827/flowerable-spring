package com.flowerable.spring.dto.payment;

import lombok.Getter;

@Getter
public class PaymentConfirmReq {
    private Integer amount;
    private String orderId;   // 주문번호 = orderNumber
    private String paymentKey;
}
