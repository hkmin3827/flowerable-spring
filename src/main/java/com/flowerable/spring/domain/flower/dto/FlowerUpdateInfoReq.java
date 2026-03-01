package com.flowerable.spring.domain.flower.dto;

import com.flowerable.spring.domain.flower.constant.Season;

// 관리자용
public record FlowerUpdateInfoReq(
        String name,
        String floralLang,
        Season category,
        String imageUrl
) {}
