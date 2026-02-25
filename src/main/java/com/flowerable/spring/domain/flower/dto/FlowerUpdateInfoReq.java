package com.flowerable.spring.dto.flower;

import com.flowerable.spring.constant.flower.Season;

public record FlowerUpdateInfoReq(
        String name,
        String floralLang,
        Season category,
        String imageUrl
) {}
