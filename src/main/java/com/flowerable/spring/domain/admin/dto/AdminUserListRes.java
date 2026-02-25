package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.auth.AccountStatus;

import java.time.LocalDateTime;

public interface AdminUserListRes {
    Long getId();
    String getAccountEmail();
    String getAccountTelnum();
    AccountStatus getAccountStatus();
    String getName();
    boolean isActive();
    LocalDateTime getCreatedAt();
}
