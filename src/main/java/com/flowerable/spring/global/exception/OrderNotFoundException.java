package com.flowerable.spring.global.exception;

import com.flowerable.spring.global.constant.ErrorCode;

public class OrderNotFoundException extends CustomException {
    public OrderNotFoundException(){
      super(ErrorCode.ORDER_NOT_FOUND);
    }
}
