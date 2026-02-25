package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.flower.Season;

public interface AdminFlowerListRes {
    Long getId();
    String getName();
    String getFloralLang();
    String getImageUrl();
    Season getCategory();
    boolean isActive();
}
