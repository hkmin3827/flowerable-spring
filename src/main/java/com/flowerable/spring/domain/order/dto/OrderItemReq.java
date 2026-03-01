package com.flowerable.spring.domain.order.dto;

import com.flowerable.spring.domain.shopflower.constant.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReq {
    private Long shopFlowerId;
    private Integer quantity;
    private Color flowerColor;
}