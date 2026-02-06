package com.flowerable.spring.dto.user;

import com.flowerable.spring.constant.auth.Provider;
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
}
