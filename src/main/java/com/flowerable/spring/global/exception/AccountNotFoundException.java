package com.flowerable.spring.global.exception;

import com.flowerable.spring.global.constant.ErrorCode;

public class AccountNotFoundException extends CustomException {
    public AccountNotFoundException() {
        super(ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
