package com.flowerable.spring.dto.auth;

import com.flowerable.spring.constant.LoginType;
import lombok.Getter;

// SHOP & USER 공통
@Getter
public class LoginReqDto {
    private String email;
    private String password;
    private LoginType loginType;
}
