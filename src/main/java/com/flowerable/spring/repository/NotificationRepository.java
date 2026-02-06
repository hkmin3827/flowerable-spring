package com.flowerable.spring.repository;

import com.flowerable.spring.constant.notification.NotificationReceiverType;
import com.flowerable.spring.entity.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        select n
        from Notification n
        where n.receiverType = :receiverType
          and n.receiverId = :receiverId
          and n.read = false
        order by n.createdAt desc
    """)
    Page<Notification> findUnreadNotifications(
            @Param("receiverType") NotificationReceiverType receiverType,
            @Param("receiverId") Long receiverId,
            Pageable pageable
    );
}