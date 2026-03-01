package com.flowerable.spring.global.exception;

import com.flowerable.spring.global.constant.ErrorCode;

public class NotificationNotFoundException extends CustomException {
    public NotificationNotFoundException()
    {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
