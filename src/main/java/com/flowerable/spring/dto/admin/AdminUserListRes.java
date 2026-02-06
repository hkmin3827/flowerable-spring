package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.auth.AccountStatus;

public interface AdminUserListRes {
    Long getId();
    String getAccountEmail();
    AccountStatus getAccountStatus();
    String getName();
    boolean isActive();
}
