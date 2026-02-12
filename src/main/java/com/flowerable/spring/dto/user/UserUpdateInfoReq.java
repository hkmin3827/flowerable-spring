package com.flowerable.spring.dto.user;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateInfoReq {
    private String name;

    @Pattern(
            regexp = "^010[0-9]{8}$",
            message = "전화번호는 010으로 시작하는 11자리 숫자여야 합니다."
    )
    private String telnum;
}
