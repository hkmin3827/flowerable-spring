package com.flowerable.spring.exception;

import com.flowerable.spring.constant.common.ErrorCode;

public class OrderNotFoundException extends CustomException {
    public OrderNotFoundException(){
      super(ErrorCode.ORDER_NOT_FOUND);
    }
}
