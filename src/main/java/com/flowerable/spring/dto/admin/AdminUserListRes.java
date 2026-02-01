package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.AccountStatus;

public interface AdminUserListRes {
    Long getId();
    String getAccountEmail();
    AccountStatus getAccountStatus();
    String getName();
    boolean isActive();
}
