package com.flowerable.spring.domain.auth.dto;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.auth.constant.Provider;
import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthRes {
    private final Long id;
    private final Role role;
    private final String name;
    private final String profileImgUrl;
    private final String accessToken;
    private final String refreshToken;
    private final Provider provider;
    private final AccountStatus accountStatus;
    private final ShopStatus shopStatus;

    public static AuthRes requireEmailAndTelnum(Long accountId, Provider provider, AccountStatus status) {
        return AuthRes.builder()
                .id(accountId)
                .role(Role.ROLE_USER)
                .provider(provider)
                .accessToken(null)
                .refreshToken(null)
                .accountStatus(status)
                .shopStatus(null)
                .build();
    }
}