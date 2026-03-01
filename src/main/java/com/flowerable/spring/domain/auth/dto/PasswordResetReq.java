package com.flowerable.spring.domain.auth.dto;

import lombok.Getter;

@Getter
public class PasswordResetReq {
    private String token;
    private String newPassword;
}