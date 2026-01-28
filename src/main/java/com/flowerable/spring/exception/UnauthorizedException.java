package com.flowerable.spring.exception;

import com.flowerable.spring.constant.ErrorCode;

public class UnauthorizedException extends CustomException{
    public UnauthorizedException(){
        super(ErrorCode.UNAUTHORIZED);
    }
}
