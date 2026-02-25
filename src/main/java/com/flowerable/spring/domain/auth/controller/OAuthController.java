package com.flowerable.spring.controller.auth;

import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.dto.auth.AuthReq;
import com.flowerable.spring.dto.auth.AuthRes;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.service.auth.OAuthStateService;
import com.flowerable.spring.service.auth.UserAuthService;
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

    /**
     * OAuth 인증 코드로 JWT 토큰 발급
     */
    @PostMapping("/token")
    public ResponseEntity<AuthRes> exchangeToken(
            @RequestBody AuthReq.OAuthTokenExchange req
    ) {
        // 1. 인증 코드 검증 및 소비
        String[] oauthInfo = oauthStateService.validateAndConsumeCode(req.code());

        if (oauthInfo == null) {
            throw new CustomException(ErrorCode.INVALID_OAUTH_CODE);
        }

        String provider = oauthInfo[0];
        String providerId = oauthInfo[1];

        // 2. OAuth 회원가입 또는 로그인 처리
        AuthRes authRes = userAuthService.processOAuthLogin(
                Provider.valueOf(provider.toUpperCase()),
                providerId
        );

        return ResponseEntity.ok(authRes);
    }

    /**
     * OAuth 추가 정보 입력 완료
     */
    @PostMapping("/complete")
    public ResponseEntity<AuthRes> completeOAuthSignup(
            @Valid @RequestBody AuthReq.OAuthComplete req
    ) {
        AuthRes authRes = userAuthService.completeOAuthSignup(req);
        return ResponseEntity.ok(authRes);
    }
}