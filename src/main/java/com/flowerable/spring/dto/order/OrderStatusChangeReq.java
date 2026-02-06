package com.flowerable.spring.dto.order;

import com.flowerable.spring.constant.order.OrderCancelReason;
import com.flowerable.spring.constant.order.OrderStatus;

public record OrderStatusChangeReq(
        OrderStatus status,
        OrderCancelReason cancelReason
) {}