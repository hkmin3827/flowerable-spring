package com.flowerable.spring.dto.shopimage;

import java.util.List;

public record ShopImageCreateReq(
        List<String> imageUrls
) {}
