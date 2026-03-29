package com.flowerable.spring.application.shopflower.dto;

import com.flowerable.spring.domain.shopflower.Color;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

@Getter
public class ShopFlowerUpdateReq {
    @Positive
    private Integer price;
    private List<Color> colors;
}
