package com.flowerable.spring.application.wrappingoption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WrappingOptionRes {
    private final Long shopId;
    private final List<String> colorNames;
    private final Integer price;
}