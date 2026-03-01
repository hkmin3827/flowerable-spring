package com.flowerable.spring.domain.shopflower.dto;

import com.flowerable.spring.domain.shopflower.constant.Color;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

@Getter
public class ShopFlowerUpdateReq {
    @Positive
    private Integer price;
    private List<Color> colors;
}
