package com.flowerable.spring.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegionRes {
    private final String code;        // SEOUL
    private final String description; // 서울특별시
}