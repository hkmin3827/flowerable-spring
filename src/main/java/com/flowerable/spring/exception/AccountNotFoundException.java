package com.flowerable.spring.exception;

import com.flowerable.spring.constant.common.ErrorCode;

public class AccountNotFoundException extends CustomException {
    public AccountNotFoundException() {
        super(ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
