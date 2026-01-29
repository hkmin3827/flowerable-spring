package com.flowerable.spring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "샵을 찾을 수 없습니다."),
    INACTIVE_ACCOUNT(HttpStatus.FORBIDDEN,"비활성된 계정입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다."),  // 401 UNAUTHORIZED
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효한 토큰이 아닙니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효한 REFRESH 토큰이 아닙니다."),
    FLOWER_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 꽃입니다."),
    FLOWER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 꽃이 존재하지 않습니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호가 필요합니다"),
    ACCOUNT_ALREADY_ACTIVE(HttpStatus.CONFLICT, "이미 활성화된 회원 입니다."),
    ACCOUNT_ALREADY_INACTIVE(HttpStatus.CONFLICT, "이미 비활성화된 회원 입니다."),
    INVALID_UPLOAD_FOLDER(HttpStatus.BAD_REQUEST,"허용되지 않은 업로드 경로입니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "잘못된 파일명입니다."),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "잘못된 파일형식입니다."),
    INVALID_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "잘못된 로그인 타입입니다."),
    ADMIN_SIGNUP_NOT_ALLOWED(HttpStatus.FORBIDDEN, "관리자 계정은 회원가입할 수 없습니다."),
    ADMIN_WITHDRAW_NOT_ALLOWED(HttpStatus.FORBIDDEN, "관리자 계정은 회원탈퇴할 수 없습니다."),
    LOGIN_ROLE_MISMATCH(HttpStatus.BAD_REQUEST,"계정 유형을 잘못 선택하셨습니다."),
    INVALID_ROLE(HttpStatus.UNAUTHORIZED, "존재하지 않는 계정 유형입니다.");

    private final HttpStatus status;
    private final String message;
}