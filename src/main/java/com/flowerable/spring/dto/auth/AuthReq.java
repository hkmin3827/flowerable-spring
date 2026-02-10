package com.flowerable.spring.dto.auth;

import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.auth.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class AuthReq {
    @Getter @Setter
    public static class UserSignup {
        @Email
        @NotBlank
        String email;

        @NotBlank
        String password;

        @NotBlank
        String name;

        @Pattern(regexp = "^[0-9]{10,11}$")
        String telnum;
    }

    @Getter @Setter
    public static class ShopSignup {
        @Email
        @NotBlank
        String email;

        @NotBlank
        String password;

        @NotBlank
        String shopName;

        @NotBlank
        String telnum;

        @NotBlank
        String address;

        @NotBlank
        String regionCode;

        @NotBlank
        String districtCode;
    }
    @Getter @Setter
    public static class Login{
        @NotNull
        private Role role;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter @Setter @AllArgsConstructor
    public static class OAuth2Login{
        @NotNull
        private Provider provider;

        @NotBlank
        private String providerId;

        private String email;  // optional
        private String name;   // optional
    }

    @Getter @Setter
    public static class Withdraw{
        private String confirmText;
        private String password;
    }


    public record OAuthTokenExchange(
            String code,
            String provider
    ) {}

    /**
     * OAuth 추가 정보 입력
     */
    public record OAuthComplete(
            Long accountId,
            String email,
            String telnum,
            String name
    ) {}
}
