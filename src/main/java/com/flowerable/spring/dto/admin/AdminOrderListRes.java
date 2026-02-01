package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.OrderCancelBy;
import com.flowerable.spring.constant.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminOrderListRes {
    private final Long orderId;
    private final OrderStatus status;

    private final Long userId;
    private final Long shopId;

    private final Integer totalFlowerPrice;
    private final Integer totalPrice;
    private final long totalCount;

    private final LocalDateTime createdAt;
    private final LocalDateTime canceledAt;

    private final OrderCancelBy canceledBy;
}