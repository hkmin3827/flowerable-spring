package com.flowerable.spring.domain.auth.oauth2.userInfo;

import com.flowerable.spring.domain.auth.constant.Provider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
    Provider getProvider();
}
