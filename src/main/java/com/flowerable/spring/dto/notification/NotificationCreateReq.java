package com.flowerable.spring.dto.notification;

import com.flowerable.spring.constant.NotificationReceiverType;
import com.flowerable.spring.constant.NotificationType;

public record NotificationCreateReq (
        NotificationReceiverType receiverType,
        Long receiverId,
        NotificationType type,
        String title,
        String content,
        Long referenceId
){
}
