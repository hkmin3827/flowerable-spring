package com.flowerable.spring.domain.auth.dto;

import com.flowerable.spring.domain.auth.constant.Provider;
import com.flowerable.spring.domain.auth.constant.Role;
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

        @Pattern(
                regexp = "^010[0-9]{8}$",
                message = "전화번호는 010으로 시작하는 11자리 숫자여야 합니다."
        )
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

        @Pattern(
                regexp = "^[0-9]{9,11}$",
                message = "전화번호는 숫자 9~11자리여야 합니다."
        )
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
            @NotNull Long accountId,
            @Email @NotBlank String email,
            @Pattern(
                    regexp = "^010[0-9]{8}$",
                    message = "전화번호는 010으로 시작하는 11자리 숫자여야 합니다."
            ) String telnum,
            @NotBlank String name
    ) {}
}
