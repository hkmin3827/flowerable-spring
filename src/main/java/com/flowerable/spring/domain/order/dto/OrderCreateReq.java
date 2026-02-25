package com.flowerable.spring.dto.order;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateReq {
    private String wrappingColorName;
    private Integer wrappingExtraPrice;
    private List<OrderItemReq> orderItems;
    private String message;
}
