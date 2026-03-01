package com.flowerable.spring.domain.auth.controller;

import com.flowerable.spring.domain.auth.constant.Provider;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.auth.dto.AuthReq;
import com.flowerable.spring.domain.auth.dto.AuthRes;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.domain.auth.service.OAuthStateService;
import com.flowerable.spring.domain.auth.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthStateService oauthStateService;
    private final UserAuthService userAuthService;

    @PostMapping("/token")
    public ResponseEntity<AuthRes> exchangeToken(
            @RequestBody AuthReq.OAuthTokenExchange req
    ) {
        String[] oauthInfo = oauthStateService.validateAndConsumeCode(req.code());

        if (oauthInfo == null) {
            throw new CustomException(ErrorCode.INVALID_OAUTH_CODE);
        }

        String provider = oauthInfo[0];
        String providerId = oauthInfo[1];

        AuthRes authRes = userAuthService.processOAuthLogin(
                Provider.valueOf(provider.toUpperCase()),
                providerId
        );

        return ResponseEntity.ok(authRes);
    }

    @PostMapping("/complete")
    public ResponseEntity<AuthRes> completeOAuthSignup(
            @Valid @RequestBody AuthReq.OAuthComplete req
    ) {
        AuthRes authRes = userAuthService.completeOAuthSignup(req);
        return ResponseEntity.ok(authRes);
    }
}