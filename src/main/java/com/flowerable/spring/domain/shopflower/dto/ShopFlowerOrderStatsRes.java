package com.flowerable.spring.domain.shopflower.dto;

public record ShopFlowerOrderStatsRes(
        int rank,
        String flowerName,
        Long orderCount
) {}