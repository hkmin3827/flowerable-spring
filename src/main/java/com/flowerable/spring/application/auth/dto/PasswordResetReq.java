package com.flowerable.spring.application.auth.dto;

import lombok.Getter;

@Getter
public class PasswordResetReq {
    private String token;
    private String newPassword;
}