package com.flowerable.spring.oauth2.userInfo;

import com.flowerable.spring.constant.auth.Provider;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo{
    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes){this.attributes = attributes;}

    @Override
    public String getProviderId() {return attributes.get("sub").toString();}

    @Override
    public String getEmail() {return attributes.get("email").toString();}

    @Override
    public String getName() {return attributes.get("name").toString();}

    @Override
    public Provider getProvider() {return Provider.GOOGLE;}
}
