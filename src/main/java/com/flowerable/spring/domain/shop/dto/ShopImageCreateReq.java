package com.flowerable.spring.domain.shop.dto;

import java.util.List;

public record ShopImageCreateReq(
        List<String> imageUrls
) {}
