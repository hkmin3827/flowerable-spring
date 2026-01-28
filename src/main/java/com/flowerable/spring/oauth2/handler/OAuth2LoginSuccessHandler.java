//package com.flowerable.spring.oauth2.handler;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2LoginSuccessHandler
//        extends SimpleUrlAuthenticationSuccessHandler {
//
//    @Value("${app.frontend.base-url}")
//    private String frontBaseUrl;
//
//    @Override
//    public void onAuthenticationSuccess(
//            HttpServletRequest req,
//            HttpServletResponse res,
//            Authentication authentication
//    ) throws IOException {
//
//        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//        Map<String, Object> attributes = oauth2User.getAttributes();
//
//        String provider = resolveProvider(attributes);
//
//        /*
//         * ❗ 여기서 하는 일은 딱 하나
//         * "OAuth 인증은 성공했다"는 사실만 프론트에 전달
//         */
//        String redirectUrl =
//                frontBaseUrl +
//                        "/oauth/callback" +
//                        "?provider=" + provider;
//
//        getRedirectStrategy()
//                .sendRedirect(req, res, redirectUrl);
//    }
//
//    /**
//     * provider 판별 (Security 전용 로직)
//     */
//    private String resolveProvider(Map<String, Object> attributes) {
//
//        if (attributes.containsKey("sub")) {
//            return "google";
//        }
//
//        if (attributes.containsKey("kakao_account")) {
//            return "kakao";
//        }
//
//        if (attributes.containsKey("response")) {
//            return "naver";
//        }
//
//        throw new IllegalStateException("Unsupported OAuth provider");
//    }
//}
