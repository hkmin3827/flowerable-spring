package com.flowerable.spring.application.order.dto;

import com.flowerable.spring.domain.order.constant.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderDetailRes {
    private final Long orderId;
    private final String orderNumber;
    private final OrderStatus status;

    private final Long userId;
    private final Long shopId;
    private String opponentTelnum;
    private String shopAddress;

    private final Integer totalFlowerPrice;
    private final Integer wrappingExtraPrice;
    private final Integer totalPrice;

    private final String wrappingColorName;
    private final LocalDateTime createdAt;
    private final LocalDateTime canceledAt;

    private final List<OrderItemRes> items;
    private String message;

    private final String shopName;
    private final String userName;

    private final String cancelReason;
    private final String cancelBy;
}