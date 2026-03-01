package com.flowerable.spring.domain.shopflower.dto;

import com.flowerable.spring.domain.shopflower.constant.Color;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ShopFlowerRegReq {

    @NotNull
    private Long flowerId;

    @NotEmpty
    private List<Color> colors;

    @NotNull
    @Positive
    private Integer price;
}