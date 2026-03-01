package com.flowerable.spring.domain.wrappingoption.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class WrappingOptionReq {
    private List<String> colorNames;
    private Integer price;
}
