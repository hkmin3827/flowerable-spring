package com.flowerable.spring.application.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateReq {
    private String wrappingColorName;
    private Integer wrappingExtraPrice;
    private List<OrderItemReq> orderItems;
    private String message;
}
