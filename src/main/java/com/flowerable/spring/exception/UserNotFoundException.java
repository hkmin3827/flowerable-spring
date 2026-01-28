package com.flowerable.spring.exception;

import com.flowerable.spring.constant.ErrorCode;

public class UserNotFoundException extends CustomException{
    public UserNotFoundException(){
        super(ErrorCode.USER_NOT_FOUND);
    }
}
