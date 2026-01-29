package com.flowerable.spring.controller;

import com.flowerable.spring.dto.auth.AuthReqDto;
import com.flowerable.spring.dto.auth.AuthResDto;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.auth.AuthService;
import com.flowerable.spring.service.auth.ShopAuthService;
import com.flowerable.spring.service.auth.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserAuthService userAuthService;
    private final ShopAuthService shopAuthService;

    @PostMapping("/users/signup")
    public AuthResDto userSignup(
            @RequestBody AuthReqDto.UserSignup dto
    ) {
        return userAuthService.signup(dto);
    }

    @PostMapping("/shops/signup")
    public AuthResDto shopSignup(
            @RequestBody AuthReqDto.ShopSignup dto
    ) {
        return shopAuthService.signup(dto);
    }
    @PostMapping("/login")
    public AuthResDto login(@RequestBody AuthReqDto.Login req) {
        return authService.login(req);
    }

    @PostMapping("/oauth/login")
    public AuthResDto oauthLogin(@RequestBody AuthReqDto.OAuth2Login dto) {
        return authService.oauth2Login(dto);
    }

    @PostMapping("/withdraw")
    public void withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AuthReqDto.Withdraw req
    ) {
        authService.withdraw(userDetails.getId(), userDetails.getRole(), req);
    }

    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        authService.logout(userDetails.getId());
    }

    @PostMapping("/reissue")
    public AuthResDto reissue(@RequestHeader("Refresh-Token") String refreshToken) {
        return authService.reissue(refreshToken);
    }
}
