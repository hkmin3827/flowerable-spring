package com.flowerable.spring.application.notification.dto;

import com.flowerable.spring.domain.notification.NotificationReceiverType;
import com.flowerable.spring.domain.notification.NotificationType;

public record NotificationCreateReq (
        NotificationReceiverType receiverType,
        Long receiverId,
        NotificationType type,
        String title,
        String content,
        Long referenceId
){
}
