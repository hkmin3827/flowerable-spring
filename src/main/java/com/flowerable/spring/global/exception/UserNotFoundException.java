package com.flowerable.spring.global.exception;

import com.flowerable.spring.global.constant.ErrorCode;

public class UserNotFoundException extends CustomException{
    public UserNotFoundException(){
        super(ErrorCode.USER_NOT_FOUND);
    }
}
