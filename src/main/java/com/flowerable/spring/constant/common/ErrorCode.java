package com.flowerable.spring.constant.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    TELNUM_DUPLICATED(HttpStatus.CONFLICT, "이미 일반 회원으로 가입한 전화번호입니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND,"계정을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "샵을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
    SUSPENDED_ACCOUNT(HttpStatus.FORBIDDEN,"접근 제한된 계정입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효한 토큰이 아닙니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효한 REFRESH 토큰이 아닙니다."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "유효하지 않은 지역 주소입니다."),
    FLOWER_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 꽃입니다."),
    FLOWER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 꽃이 존재하지 않습니다."),
    FLOWER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "관리자에 의해 비활성화된 꽃입니다."),
    SHOP_FLOWER_ALREADY_REGISTER(HttpStatus.CONFLICT, "이미 매장에 등록된 꽃입니다."),
    SHOP_FLOWER_NOT_REGISTER(HttpStatus.CONFLICT, "매장에 등록된 꽃이 아닙니다."),
    SHOP_FLOWER_ALREADY_ONSALE(HttpStatus.CONFLICT, "이미 판매 중인 꽃입니다."),
    SHOP_FLOWER_ALREADY_STOPSALE(HttpStatus.CONFLICT, "이미 숨김 처리된 꽃입니다."),
    SHOP_FLOWER_NOT_ON_SALE(HttpStatus.NOT_FOUND, "매장에서 판매 중인 꽃이 아닙니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호가 필요합니다"),
    INVALID_SHOP_STATUS(HttpStatus.BAD_REQUEST, "샵 상태 변경 오류, 유효하지 않은 요청입니다."),
    SUSPEND_ORDER_ACCOUNT(HttpStatus.FORBIDDEN, "주문 기능이 이용 불가한 계정입니다."),
    INVALID_ACCOUNT_STATUS(HttpStatus.BAD_REQUEST, "계정 상태 변경 오류, 유효하지 않은 요청입니다."),
    ACCOUNT_ALREADY_ACTIVE(HttpStatus.CONFLICT, "이미 활성화된 회원 입니다."),
    ACCOUNT_ALREADY_INACTIVE(HttpStatus.CONFLICT, "이미 비활성화된 회원 입니다."),
    INVALID_FLOWER_COLORS(HttpStatus.BAD_REQUEST, "색상은 최소 1개 이상이어야 합니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    FAIL_CHANGE_ORDER_STATUS(HttpStatus.BAD_REQUEST, "현재 주문 상태에서 변경 불가한 요청입니다."),
    INVALID_UPLOAD_FOLDER(HttpStatus.BAD_REQUEST,"허용되지 않은 업로드 경로입니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "잘못된 파일명입니다."),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "잘못된 파일형식입니다."),
    INVALID_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "잘못된 로그인 타입입니다."),
    INVALID_RECEIVER_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 receiver 타입입니다."),
    ADMIN_SIGNUP_NOT_ALLOWED(HttpStatus.FORBIDDEN, "관리자 계정은 회원가입할 수 없습니다."),
    ADMIN_WITHDRAW_NOT_ALLOWED(HttpStatus.FORBIDDEN, "관리자 계정은 회원탈퇴할 수 없습니다."),
    LOGIN_ROLE_MISMATCH(HttpStatus.BAD_REQUEST,"계정 유형을 잘못 선택하셨습니다."),
    INVALID_ROLE(HttpStatus.UNAUTHORIZED, "존재하지 않는 계정 유형입니다."),
    INVALID_COORDINATE(HttpStatus.BAD_REQUEST, "유효하지 않은 좌표 값입니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태 요청입니다."),
    WRAPPING_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "설정된 포장지 옵션이 없습니다."),
    ORDER_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "이미 접수된 주문건은 취소가 불가합니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소 완료된 주문입니다."),
    ROLE_NOT_USER(HttpStatus.BAD_REQUEST, "USER 계정이 아닙니다."),
    ROLE_NOT_SHOP(HttpStatus.BAD_REQUEST, "SHOP 계정이 아닙니다."),
    CANCEL_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "취소 사유는 필수 값입니다."),
    IMAGE_ALREADY_NOT_THUMBNAIL(HttpStatus.CONFLICT, "이미 대표 이미지가 아닌 사진입니다."),
    IMAGE_ALREADY_THUMBNAIL(HttpStatus.CONFLICT, "이미 대표 이미지로 설정된 사진입니다."),
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "채팅방 인원에 속해있지 않습니다."),
    PROFILE_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "프로필 필수 값이 입력되지 않았습니다."),
    INVALID_OAUTH_CODE(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 인증 코드입니다.");

    private final HttpStatus status;
    private final String message;
}