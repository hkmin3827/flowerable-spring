package com.flowerable.spring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다."),  // 401 UNAUTHORIZED
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호가 필요합니다"),
    USER_ALREADY_ACTIVE(HttpStatus.CONFLICT, "이미 활성화된 회원 입니다."),
    USER_ALREADY_INACTIVE(HttpStatus.CONFLICT, "이미 비활성화된 회원 입니다."),
    INVALID_UPLOAD_FOLDER(HttpStatus.BAD_REQUEST,"허용되지 않은 업로드 경로입니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "잘못된 파일명입니다."),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "잘못된 파일형식입니다.");

    private final HttpStatus status;
    private final String message;
}