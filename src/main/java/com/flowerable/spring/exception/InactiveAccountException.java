package com.flowerable.spring.exception;

import com.flowerable.spring.constant.ErrorCode;

public class InactiveAccountException extends CustomException{
    public InactiveAccountException(){
        super(ErrorCode.INACTIVE_ACCOUNT);
    }
}
