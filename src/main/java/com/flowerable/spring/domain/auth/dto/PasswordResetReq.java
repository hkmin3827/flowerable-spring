package com.flowerable.spring.dto.auth;

import lombok.Getter;

@Getter
public class PasswordResetReq {
    private String token;
    private String newPassword;
}