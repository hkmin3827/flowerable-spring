package com.flowerable.spring.application.shopflower.dto;

import com.flowerable.spring.domain.shopflower.Color;
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