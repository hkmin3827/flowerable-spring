package com.flowerable.spring.global.exception;

import com.flowerable.spring.global.constant.ErrorCode;

public class SuspendedAccountException extends CustomException{
    public SuspendedAccountException(){
        super(ErrorCode.SUSPENDED_ACCOUNT);
    }
}
