package com.flowerable.spring.dto.order;

import com.flowerable.spring.constant.shopflower.Color;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemRes {
    private final Long shopFlowerId;
    private final String flowerName;
    private final Color flowerColor;
    private final Integer itemTotalPrice;  // 서비스로직에서 계산
    private final Integer quantity;
}