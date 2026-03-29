package com.flowerable.spring.application.admin.dto;

import com.flowerable.spring.domain.auth.constant.AccountStatus;

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
