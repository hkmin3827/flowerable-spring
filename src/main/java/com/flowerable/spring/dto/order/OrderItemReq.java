package com.flowerable.spring.dto.order;

import com.flowerable.spring.constant.Color;
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