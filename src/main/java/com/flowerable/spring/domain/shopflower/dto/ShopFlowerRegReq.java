package com.flowerable.spring.dto.shopflower;

import com.flowerable.spring.constant.shopflower.Color;
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
    private Long flowerId;   // 선택한 꽃 종류

    @NotEmpty
    private List<Color> colors; // 보유 색상 목록

    @NotNull
    @Positive
    private Integer price;   // 판매 가격
}