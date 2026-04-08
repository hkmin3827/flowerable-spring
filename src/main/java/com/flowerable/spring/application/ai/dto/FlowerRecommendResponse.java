package com.flowerable.spring.application.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FlowerRecommendResponse(

        String recommendation,

        List<FlowerItem> flowers,

        List<ShopItem> shops,

        String phase
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FlowerItem(
            String name,
            String floralLang,
            String role   // 메인, 베스트, 서브
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ShopItem(
            String shopName,
            String address,
            String district,
            List<String> availableFlowers
    ) {}
}
