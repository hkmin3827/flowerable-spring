package com.flowerable.spring.dto.order;

import com.flowerable.spring.constant.order.OrderStatus;
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

    private final Integer totalFlowerPrice;
    private final Integer wrappingExtraPrice;
    private final Integer totalPrice;

    private final String wrappingColorName;
    private final LocalDateTime createdAt;
    private final LocalDateTime canceledAt;

    private final List<OrderItemRes> items;
    private String message;

    private String shopName;
    private String userName;
}