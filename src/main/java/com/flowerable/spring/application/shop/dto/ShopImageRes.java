package com.flowerable.spring.application.shop.dto;


import com.flowerable.spring.domain.shop.ShopImage;

import java.time.LocalDateTime;

public record ShopImageRes(
        Long id,
        String imageUrl,
        boolean isThumbnail,
        LocalDateTime createdAt
) {
    public static ShopImageRes from(ShopImage image) {
        return new ShopImageRes(
                image.getId(),
                image.getImageUrl(),
                image.getIsThumbnail(),
                image.getCreatedAt()
        );
    }
}