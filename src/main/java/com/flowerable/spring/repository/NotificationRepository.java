package com.flowerable.spring.repository;

import com.flowerable.spring.constant.notification.NotificationReceiverType;
import com.flowerable.spring.constant.notification.NotificationType;
import com.flowerable.spring.entity.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        select n
        from Notification n
        where n.receiverType = :receiverType
          and n.receiverId = :receiverId
          and n.isRead = false
        order by n.updatedAt desc
    """)
    Page<Notification> findUnreadNotifications(
            @Param("receiverType") NotificationReceiverType receiverType,
            @Param("receiverId") Long receiverId,
            Pageable pageable
    );

    // 채팅방 입장 시 알림 읽음 처리
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Notification n
        set n.isRead = true
        where n.type = :type
          and n.receiverId = :receiverId
          and n.referenceId = :referenceId
          and n.isRead = false
    """)
    int markAsReadByTypeAndReceiverIdAndReferenceId(
            @Param("type") NotificationType type,
            @Param("receiverId") Long receiverId,
            @Param("referenceId") Long referenceId
    );

    Optional<Notification> findByTypeAndReceiverTypeAndReceiverIdAndReferenceId(
            NotificationType type,
            NotificationReceiverType receiverType,
            Long receiverId,
            Long referenceId
    );
}