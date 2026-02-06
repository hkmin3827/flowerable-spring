package com.flowerable.spring.dto.notification;

import com.flowerable.spring.constant.notification.NotificationReceiverType;
import com.flowerable.spring.constant.notification.NotificationType;

public record NotificationCreateReq (
        NotificationReceiverType receiverType,
        Long receiverId,
        NotificationType type,
        String title,
        String content,
        Long referenceId
){
}
