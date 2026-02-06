package com.flowerable.spring.dto.flower;

import com.flowerable.spring.constant.flower.Season;
import com.flowerable.spring.entity.flower.Flower;
import lombok.Getter;

@Getter
public class FlowerRes {
    private final Long id;
    private final String name;
    private final String floralLang;
    private final String imageUrl;
    private final Season category;
    private final boolean active;

    public FlowerRes(Flower flower){
        this.id = flower.getId();
        this.name = flower.getName();
        this.floralLang = flower.getFloralLang();
        this.imageUrl = flower.getImageUrl();
        this.category = flower.getCategory();
        this.active = flower.getActive();
    }
}
