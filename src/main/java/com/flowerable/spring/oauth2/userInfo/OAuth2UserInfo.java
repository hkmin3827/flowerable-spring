package com.flowerable.spring.oauth2.userInfo;

import com.flowerable.spring.constant.Provider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
    Provider getProvider();
}
