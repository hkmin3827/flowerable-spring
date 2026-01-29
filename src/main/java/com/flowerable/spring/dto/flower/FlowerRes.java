package com.flowerable.spring.dto.flower;

import com.flowerable.spring.constant.Season;
import com.flowerable.spring.entity.flower.Flower;
import lombok.Getter;

@Getter
public class FlowerRes {
    private Long id;
    private String name;
    private String floralLang;
    private String imageUrl;
    private Season category;

    public FlowerRes(Flower flower){
        this.id = flower.getId();
        this.name = flower.getName();
        this.floralLang = flower.getFloralLang();
        this.imageUrl = flower.getImageUrl();
        this.category = flower.getCategory();
    }
}
