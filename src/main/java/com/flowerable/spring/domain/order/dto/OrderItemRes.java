package com.flowerable.spring.domain.order.dto;

import com.flowerable.spring.domain.shopflower.constant.Color;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemRes {
    private final Long shopFlowerId;
    private final String flowerName;
    private final Color flowerColor;
    private final Integer flowerBasePrice;
    private final Integer itemTotalPrice;  // 서비스로직에서 계산
    private final Integer quantity;
}