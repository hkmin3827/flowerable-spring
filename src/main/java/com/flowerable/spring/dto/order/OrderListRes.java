package com.flowerable.spring.dto.order;

import com.flowerable.spring.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderListRes {

    private final Long orderId;
    private final OrderStatus status;

    private final Integer totalPrice;
    private final LocalDateTime createdAt;

    private final long totalCount;  // 총수량
}