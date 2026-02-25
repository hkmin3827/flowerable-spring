package com.flowerable.spring.dto.notification;

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
