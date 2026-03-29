package com.flowerable.spring.application.admin.dto;

import com.flowerable.spring.domain.flower.Season;

public interface AdminFlowerListRes {
    Long getId();
    String getName();
    String getFloralLang();
    String getImageUrl();
    Season getCategory();
    boolean isActive();
}
