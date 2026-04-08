package com.flowerable.spring.application.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatBotRecommendRes(

        String phase,
        String recommendation,
        List<FlowerItem> flowers,
        List<ShopItem> shops,
        String message

) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FlowerItem(
            String name,
            String floralLang,
            String role
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ShopItem(
            Long shopId,
            String shopName,
            String address,
            String district,
            List<String> availableFlowers
    ) {}
}
