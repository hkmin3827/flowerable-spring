package com.flowerable.spring.application.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContentReq(
        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 100, message = "내용은 100자 이하로 입력해주세요.")
        String content
) {}