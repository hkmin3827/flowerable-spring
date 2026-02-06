package com.flowerable.spring.exception;

import com.flowerable.spring.constant.common.ErrorCode;

public class NotificationNotFoundException extends CustomException {
    public NotificationNotFoundException()
    {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
