package com.flowerable.spring.dto.auth;

import com.flowerable.spring.constant.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResDto {
    private Long id;
    private Role role;  // USER/ADMIN
    private String accessToken;
    private String refreshToken;
}