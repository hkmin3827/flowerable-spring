package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.order.OrderCancelBy;
import com.flowerable.spring.constant.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminOrderListRes {
    private final Long orderId;
    private final String orderNumber;
    private final OrderStatus status;

    private final String shopName;
    private final String userName;

    private final Integer totalFlowerPrice;
    private final Integer totalPrice;
    private final long totalCount;

    private final LocalDateTime createdAt;
    private final LocalDateTime canceledAt;

    private final OrderCancelBy canceledBy;
}