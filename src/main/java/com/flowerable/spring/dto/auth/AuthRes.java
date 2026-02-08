package com.flowerable.spring.dto.auth;

import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.auth.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthRes {
    private final Long id;
    private final Role role;
    private final String name;
    private final String profileImageUrl;
    private final String accessToken;
    private final String refreshToken;
    private final Provider provider;

    public static AuthRes requireEmailAndTelnum(Long accountId, Provider provider) {
        return AuthRes.builder()
                .id(accountId)
                .role(Role.ROLE_USER)     // 고정 or null 선택 가능
                .provider(provider)
                .accessToken(null)
                .refreshToken(null)
                .build();
    }
}