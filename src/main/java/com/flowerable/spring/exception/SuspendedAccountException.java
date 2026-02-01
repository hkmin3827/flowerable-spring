package com.flowerable.spring.exception;

import com.flowerable.spring.constant.ErrorCode;

public class SuspendedAccountException extends CustomException{
    public SuspendedAccountException(){
        super(ErrorCode.SUSPENDED_ACCOUNT);
    }
}
