package com.flowerable.spring.application.flower.dto;

import com.flowerable.spring.domain.flower.Season;

// 관리자용
public record FlowerUpdateInfoReq(
        String name,
        String floralLang,
        Season category,
        String imageUrl
) {}
