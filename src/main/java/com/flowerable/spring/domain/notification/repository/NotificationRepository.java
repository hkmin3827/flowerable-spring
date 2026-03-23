package com.flowerable.spring.domain.notification.repository;

import com.flowerable.spring.domain.notification.constant.NotificationReceiverType;
import com.flowerable.spring.domain.notification.constant.NotificationType;
import com.flowerable.spring.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("""
        SELECT COUNT(n) FROM Notification n
        WHERE n.receiverId = :receiverId
        AND n.isRead = false
    """)
    Long countUnreadByUserId(@Param("receiverId") Long receiverId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Notification n
        WHERE n.receiverType = :receiverType
          AND n.receiverId = :receiverId
    """)
    int deleteByReceiverTypeAndReceiverId(
            @Param("receiverType") NotificationReceiverType receiverType,
            @Param("receiverId") Long receiverId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Notification n
        WHERE n.receiverType = com.flowerable.spring.domain.notification.constant.NotificationReceiverType.SHOP
          AND n.referenceId IN :orderIds
    """)
    int deleteShopNotificationsByOrderIds(@Param("orderIds") List<Long> orderIds);
}