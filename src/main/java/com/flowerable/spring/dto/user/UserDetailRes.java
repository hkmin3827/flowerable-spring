package com.flowerable.spring.dto.user;

import com.flowerable.spring.constant.Provider;
import com.flowerable.spring.constant.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
public class UserDetailRes {
    private Long id;
    private String email;
    private Provider provider;
    private String name;
    private String providerId;
    private String telnum;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private boolean active;
}
