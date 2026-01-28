package com.flowerable.spring.controller;

import com.flowerable.spring.dto.auth.AuthReqDto;
import com.flowerable.spring.dto.auth.AuthResDto;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public AuthResDto signup(@RequestBody AuthReqDto.Signup req) {
        return authService.signup(req);
    }

    @PostMapping("/login")
    public AuthResDto login(@RequestBody AuthReqDto.Login req) {
        return authService.login(req);
    }

    @PostMapping("/oauth/login")
    public AuthResDto oauthLogin(@RequestBody AuthReqDto.OAuth2Login dto) {
        return authService.oauth2Login(dto);
    }

    @PostMapping("/{target}/{id}/withdraw")
    public void withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AuthReqDto.Withdraw req
    ) {
        authService.withdraw(userDetails.getId(), userDetails.getRole(), req);
    }

    @PostMapping("/reissue")
    public AuthResDto reissue(@RequestHeader("Refresh-Token") String refreshToken) {
        return authService.reissue(refreshToken);
    }
}
