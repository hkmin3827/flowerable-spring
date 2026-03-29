package com.flowerable.spring.application.shop.dto;

import java.util.List;

public record ShopImageCreateReq(
        List<String> imageUrls
) {}
