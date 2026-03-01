package com.flowerable.spring.domain.notification.dto;

import com.flowerable.spring.domain.notification.constant.NotificationReceiverType;
import com.flowerable.spring.domain.notification.constant.NotificationType;

public record NotificationCreateReq (
        NotificationReceiverType receiverType,
        Long receiverId,
        NotificationType type,
        String title,
        String content,
        Long referenceId
){
}
