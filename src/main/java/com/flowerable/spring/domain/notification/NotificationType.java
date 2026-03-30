package com.flowerable.spring.domain.notification;

import lombok.Getter;

@Getter
public enum NotificationType {

    ORDER_REQUESTED("새 주문이 접수되었습니다"),
    ORDER_ACCEPTED("주문이 승인되었습니다"),
    ORDER_READY("주문이 준비되었습니다"),
    ORDER_CANCELED("주문이 취소되었습니다"),
    MESSAGE_RECEIVED("새 메시지가 도착했습니다");

    private final String title;

    NotificationType(String title) {
        this.title = title;
    }
}