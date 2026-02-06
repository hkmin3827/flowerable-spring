package com.flowerable.spring.dto.auth;

import com.flowerable.spring.constant.auth.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthRes {
    private final Long id;
    private final Role role;  // USER/ADMIN
    private final String accessToken;
    private final String refreshToken;
}