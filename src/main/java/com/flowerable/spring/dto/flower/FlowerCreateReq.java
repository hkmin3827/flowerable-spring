package com.flowerable.spring.dto.flower;


import com.flowerable.spring.constant.Season;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowerCreateReq {
    private String name;
    private String floralLang;
    private String imageUrl;
    private Season category;
}
