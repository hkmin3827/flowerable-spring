package com.flowerable.spring.application.order.dto;

import com.flowerable.spring.domain.order.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderListRes {
    private final Long orderId;
    private final String orderNumber;
    private final OrderStatus status;

    private final Integer totalPrice;
    private final LocalDateTime createdAt;

    private String shopName;
    private String userName;

    private final long totalCount;

}