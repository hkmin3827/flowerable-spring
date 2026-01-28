package com.flowerable.spring.dto.auth;

import com.flowerable.spring.constant.Provider;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class UserSignupReqDto {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private Provider provider;

    @NotBlank
    private String telnum;

    private String address;

}
