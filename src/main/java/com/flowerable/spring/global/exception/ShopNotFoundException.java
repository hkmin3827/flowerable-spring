package com.flowerable.spring.global.exception;

import com.flowerable.spring.global.constant.ErrorCode;

public class ShopNotFoundException extends CustomException{
    public ShopNotFoundException(){
        super(ErrorCode.SHOP_NOT_FOUND);
    }
}
