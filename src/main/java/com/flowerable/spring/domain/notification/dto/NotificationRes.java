package com.flowerable.spring.dto.notification;

import com.flowerable.spring.domain.notification.constant.NotificationReceiverType;
import com.flowerable.spring.domain.notification.constant.NotificationType;
import com.flowerable.spring.entity.notification.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationRes {
    private final Long id;
    private final Long receiverId;
    private final NotificationReceiverType receiverType;
    private final String title;
    private final String content;
    private final NotificationType type;
    private final Long referenceId; // orderId, chatRoomId
    private final boolean read;
    private final LocalDateTime readAt;
    private final LocalDateTime createdAt;

    public NotificationRes(Notification noti){
        this.id = noti.getId();
        this.receiverId = noti.getReceiverId();
        this.receiverType = noti.getReceiverType();
        this.title = noti.getTitle();
        this.content = noti.getContent();
        this.type = noti.getType();
        this.referenceId = noti.getReferenceId();
        this.read = noti.isRead();
        this.readAt = noti.getReadAt();
        this.createdAt = noti.getCreatedAt();
    }
}
