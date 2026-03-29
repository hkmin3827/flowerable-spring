package com.flowerable.spring.application.order.dto;

import com.flowerable.spring.domain.shopflower.Color;
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