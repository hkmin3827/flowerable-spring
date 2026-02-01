package com.flowerable.spring.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateInfoReq {
    private String name;
    private String telnum;
}
