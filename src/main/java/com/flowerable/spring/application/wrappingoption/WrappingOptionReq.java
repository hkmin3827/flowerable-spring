package com.flowerable.spring.application.wrappingoption;

import lombok.Getter;

import java.util.List;

@Getter
public class WrappingOptionReq {
    private List<String> colorNames;
    private Integer price;
}
