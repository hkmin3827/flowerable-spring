package com.flowerable.spring.domain.auth.constant;

public enum AccountStatus {
    TEMP,   // 가입 미완료
    ACTIVE,   // 로그인 가능
    SUSPENDED,   // 로그인 거부 (관리자 차단, 블랙리스트)
    DELETED   // 회원 탈퇴
}
