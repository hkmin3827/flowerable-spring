package com.flowerable.spring.oauth2.userInfo;

import com.flowerable.spring.constant.Provider;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo{
    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Object accountObj = attributes.get("kakao_account");
        if (!(accountObj instanceof Map<?, ?> kakaoAccount)) {
            return null;
        }

        Object email = kakaoAccount.get("email");
        return email instanceof String ? (String) email : null;
    }

    @Override
    public String getName() {
        Object accountObj = attributes.get("kakao_account");
        if (!(accountObj instanceof Map<?, ?> kakaoAccount)) {
            return null;
        }

        Object profileObj = kakaoAccount.get("profile");
        if (!(profileObj instanceof Map<?, ?> profile)) {
            return null;
        }

        Object nickname = profile.get("nickname");
        return nickname instanceof String ? (String) nickname : null;
    }
}
