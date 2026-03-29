package com.flowerable.spring.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateRes {
    private final Long orderId;
    private final String orderNumber;
    private final Integer totalPrice;
}
