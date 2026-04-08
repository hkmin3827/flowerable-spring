package com.flowerable.spring.application.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FlowerRecommendRequest(

        @NotBlank(message = "목적(purpose)은 필수 입력값입니다.")
        @Size(max = 300, message = "내용은 300자 이하로 입력해주세요.")
        String purpose,

        String location
) {}
