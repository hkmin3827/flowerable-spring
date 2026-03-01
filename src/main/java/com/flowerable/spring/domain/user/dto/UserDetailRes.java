package com.flowerable.spring.domain.user.dto;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.auth.constant.Provider;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
public class UserDetailRes {
    private final Long id;
    private final String email;
    private final Provider provider;
    private final String name;
    private final String providerId;
    private final String telnum;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;
    private final boolean active;
    private final AccountStatus accountStatus;
}
