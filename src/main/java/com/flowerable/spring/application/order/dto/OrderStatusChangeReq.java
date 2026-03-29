package com.flowerable.spring.application.order.dto;

import com.flowerable.spring.domain.order.constant.OrderCancelReason;
import com.flowerable.spring.domain.order.constant.OrderStatus;

public record OrderStatusChangeReq(
        OrderStatus status,
        OrderCancelReason cancelReason
) {}