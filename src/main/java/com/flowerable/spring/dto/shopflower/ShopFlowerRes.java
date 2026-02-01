package com.flowerable.spring.dto.shopflower;

import com.flowerable.spring.constant.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShopFlowerRes {
    private final Long id;
    private final Long flowerId;
    private final String flowerName;
    private final Integer price;
    private final Boolean onSale;
    private final List<Color> colors;
}