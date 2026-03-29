package com.flowerable.spring.application.auth.userInfo;

import com.flowerable.spring.domain.auth.constant.Provider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
    Provider getProvider();
}
