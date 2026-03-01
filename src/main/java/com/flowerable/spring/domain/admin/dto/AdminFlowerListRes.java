package com.flowerable.spring.domain.admin.dto;

import com.flowerable.spring.domain.flower.constant.Season;

public interface AdminFlowerListRes {
    Long getId();
    String getName();
    String getFloralLang();
    String getImageUrl();
    Season getCategory();
    boolean isActive();
}
