package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.shopflower.Color;
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