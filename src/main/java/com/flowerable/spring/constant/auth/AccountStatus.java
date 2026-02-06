package com.flowerable.spring.constant.auth;

public enum AccountStatus {
    ACTIVE,   // 가입 가능
    SUSPENDED,   // 가입 거부 (관리자 차단, 블랙리스트)
    DELETED   // 회원 탈퇴
}
