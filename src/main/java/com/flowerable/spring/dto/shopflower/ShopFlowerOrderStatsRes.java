package com.flowerable.spring.dto.shopflower;

public record ShopFlowerOrderStatsRes(
        int rank,
        String flowerName,
        Long orderCount
) {}