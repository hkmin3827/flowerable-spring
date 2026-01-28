package com.flowerable.spring.dto.auth;

import com.flowerable.spring.constant.Provider;
import com.flowerable.spring.constant.Role;
import lombok.Getter;
import lombok.Setter;

public class AuthReqDto {
    @Getter @Setter
    public static class Signup{
        private Role role;
        private String email;
        private String password;
        private String name;
        private String shopName;
        private String telnum;
        private String address;
    }

    @Getter @Setter
    public static class Login{
        private Role loginType;
        private String email;
        private String password;
    }

    @Getter @Setter
    public static class OAuth2Login{
        private Provider provider;
        private String providerId;
        private String email;  // optional
        private String name;   // optional
    }

    @Getter @Setter
    public static class Withdraw{
        private String confirmText;
        private String password;
    }

}
