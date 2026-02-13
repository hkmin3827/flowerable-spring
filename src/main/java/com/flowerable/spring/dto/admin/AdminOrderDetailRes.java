package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.order.OrderCancelBy;
import com.flowerable.spring.constant.order.OrderCancelReason;
import com.flowerable.spring.constant.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminOrderDetailRes {
    private Long orderId;
    private OrderStatus status;
    private String orderNumber;

    private Long userId;
    private Long shopId;

    private String shopName;
    private String userName;

    private Integer totalFlowerPrice;
    private Integer totalPrice;

    private String wrappingColorName;
    private Integer wrappingExtraPrice;

    private LocalDateTime createdAt;
    private LocalDateTime canceledAt;

    private List<AdminOrderItemRes> items;

    private String message;   // 요청 사항

    private OrderCancelBy cancelBy;
    private OrderCancelReason cancelReason;
}