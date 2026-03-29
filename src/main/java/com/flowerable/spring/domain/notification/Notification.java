package com.flowerable.spring.domain.notification;

import com.flowerable.spring.application.notification.dto.NotificationCreateReq;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationReceiverType receiverType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Long referenceId; // orderId, chatRoomId

    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime readAt;

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Notification create(NotificationCreateReq req) {
        Notification notification = new Notification();
        notification.receiverType = req.receiverType();
        notification.receiverId = req.receiverId();
        notification.type = req.type();
        notification.title = req.title();
        notification.content = req.content();
        notification.referenceId = req.referenceId();
        notification.updatedAt = LocalDateTime.now();
        return notification;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsUnread() {
        this.isRead = false;
    }
}