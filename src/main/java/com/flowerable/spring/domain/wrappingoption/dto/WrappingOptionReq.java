package com.flowerable.spring.dto.shop;

import lombok.Getter;

import java.util.List;

@Getter
public class WrappingOptionReq {
    private List<String> colorNames;
    private Integer price;
}
