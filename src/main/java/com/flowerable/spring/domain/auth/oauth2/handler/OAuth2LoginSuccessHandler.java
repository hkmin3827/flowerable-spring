package com.flowerable.spring.domain.auth.oauth2.handler;

import com.flowerable.spring.domain.auth.constant.Provider;
import com.flowerable.spring.domain.auth.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.flowerable.spring.domain.auth.oauth2.userInfo.KakaoOAuth2UserInfo;
import com.flowerable.spring.domain.auth.oauth2.userInfo.NaverOAuth2UserInfo;
import com.flowerable.spring.domain.auth.oauth2.userInfo.OAuth2UserInfo;
import com.flowerable.spring.domain.auth.service.OAuthStateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuthStateService oAuthStateService;

    @Value("${app.frontend.base-url}")
    private String frontBaseUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication authentication
    ) throws IOException {
        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;

        OAuth2User oauth2User = token.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo userInfo = switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(oauth2User.getAttributes());
            case KAKAO -> new KakaoOAuth2UserInfo(oauth2User.getAttributes());
            case NAVER -> new NaverOAuth2UserInfo(oauth2User.getAttributes());
            default -> throw new IllegalStateException("Unsupported provider: " + provider);
        };

        // 일회용 인증 코드 생성 (Redis에 5분간 저장)
        String authCode = oAuthStateService.createAuthCode(
                provider.name().toLowerCase(),
                userInfo.getProviderId()
        );

        String redirectUrl = String.format(
                "%s/oauth/callback?code=%s&provider=%s",
                frontBaseUrl,
                authCode,
                provider.name().toLowerCase()
        );

        getRedirectStrategy().sendRedirect(req, res, redirectUrl);

    }
}