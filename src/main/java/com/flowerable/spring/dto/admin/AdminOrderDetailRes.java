package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.OrderCancelBy;
import com.flowerable.spring.constant.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminOrderDetailRes {
    private Long orderId;
    private OrderStatus status;

    private Long userId;
    private Long shopId;

    private Integer totalFlowerPrice;
    private Integer totalPrice;

    private String wrappingColorName;
    private Integer wrappingExtraPrice;

    private LocalDateTime createdAt;
    private LocalDateTime canceledAt;

    private OrderCancelBy canceledBy;

    private List<AdminOrderItemRes> items;
}