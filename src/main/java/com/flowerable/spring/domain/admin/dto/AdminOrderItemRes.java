package com.flowerable.spring.domain.admin.dto;

import com.flowerable.spring.domain.shopflower.constant.Color;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminOrderItemRes {
    private final Long orderItemId;

    private final Long shopFlowerId;
    private final String flowerName;

    private final Color flowerColor;
    private final Integer quantity;

    // 주문 스냅샷 기준
    private final Integer basePrice;
    private final Integer itemTotalPrice;
}