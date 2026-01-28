package com.flowerable.spring.exception;

import com.flowerable.spring.constant.ErrorCode;

public class ShopNotFoundException extends CustomException{
    public ShopNotFoundException(){
        super(ErrorCode.SHOP_NOT_FOUND);
    }
}
