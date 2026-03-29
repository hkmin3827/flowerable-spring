package com.flowerable.spring.application.shopflower.dto;

public record ShopFlowerOrderStatsRes(
        int rank,
        String flowerName,
        Long orderCount
) {}