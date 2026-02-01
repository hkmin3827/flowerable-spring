package com.flowerable.spring.dto.flower;


import com.flowerable.spring.constant.Season;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
// 관리자용

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowerCreateReq {
    @NotBlank
    private String name;

    private String floralLang;
    private String imageUrl;

    @NotNull
    private Season category;
}
