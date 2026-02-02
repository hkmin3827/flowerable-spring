package com.flowerable.spring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderCancelReason {

    OUT_OF_STOCK("재고 부족"),
    CUSTOMER_REQUEST("주문자 요청"),
    CANNOT_FULFILL_REQUEST("요청 사항 어려움"),
    SHOP_CLOSED("영업 종료 / 임시 휴무"),
    PRICE_ERROR("가격 오류"),
    OTHER("가게 사정");

    private final String description;
}