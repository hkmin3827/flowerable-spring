package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.auth.AccountStatus;

public interface AdminUserListRes {
    Long getId();
    String getAccount_Email();
    String getAccount_Telnum();
    AccountStatus getAccount_Status();
    String getName();
    boolean isActive();
}
