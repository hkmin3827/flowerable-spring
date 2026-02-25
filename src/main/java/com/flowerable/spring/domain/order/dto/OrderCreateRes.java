package com.flowerable.spring.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreateRes {
    private final Long orderId;
    private final String orderNumber;
    private final Integer totalPrice;
}
