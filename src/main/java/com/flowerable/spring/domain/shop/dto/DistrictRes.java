package com.flowerable.spring.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DistrictRes {
    private final String code;        // SEOUL_GANGNAM
    private final String description; // 강남구
}