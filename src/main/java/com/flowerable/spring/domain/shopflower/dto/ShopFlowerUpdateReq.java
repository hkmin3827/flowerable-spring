package com.flowerable.spring.dto.shopflower;

import com.flowerable.spring.constant.shopflower.Color;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

@Getter
public class ShopFlowerUpdateReq {
    @Positive
    private Integer price;
    private List<Color> colors;
}
