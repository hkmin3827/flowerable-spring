package com.flowerable.spring.dto.order;

import com.flowerable.spring.constant.OrderStatus;

public record OrderStatusChangeReq(
        OrderStatus status
) {}