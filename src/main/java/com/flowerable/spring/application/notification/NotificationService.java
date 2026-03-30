package com.flowerable.spring.application.notification;

import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.notification.NotificationReceiverType;
import com.flowerable.spring.domain.notification.NotificationType;
import com.flowerable.spring.application.notification.dto.NotificationCreateReq;
import com.flowerable.spring.application.notification.dto.NotificationRes;
import com.flowerable.spring.domain.notification.Notification;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.NotificationNotFoundException;
import com.flowerable.spring.infra.sse.SseEmitterManager;
import com.flowerable.spring.domain.notification.NotificationRepository;
import com.flowerable.spring.domain.shop.ShopRepository;
import com.flowerable.spring.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitterManager sseEmitterManager;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public Notification createNotification(
            NotificationCreateReq req
    ) {
        Notification notification = Notification.create(req);

        notificationRepository.save(notification);

        sendIfConnected(notification);

        return notification;
    }

    @Transactional
    public void createOrUpdateChatNotification(
            NotificationCreateReq req
    ) {
        Notification notification = notificationRepository
                .findByTypeAndReceiverTypeAndReceiverIdAndReferenceId(
                        NotificationType.MESSAGE_RECEIVED,
                        req.receiverType(),
                        req.receiverId(),
                        req.referenceId()
                )
                .orElse(null);

        if (notification == null) {
            Notification newNotification = Notification.create(req);

            notificationRepository.save(newNotification);
            sendIfConnected(newNotification);
            return;
        }

        notification.updateContent(req.content());
        notification.markAsUnread();
        sendIfConnected(notification);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long accountId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        Long receiverId = resolveReceiverId(accountId, notification.getReceiverType());

        if (!notification.getReceiverId().equals(receiverId)) {
            throw new AccessDeniedException("알림 수신자와 계정이 일치하지 않습니다.");
        }

        notification.markAsRead();
    }

    @Transactional(readOnly = true)
    public Page<NotificationRes> getUnreadNotifications(
            Long accountId,
            Role role,
            Pageable pageable
    ) {
        NotificationReceiverType receiverType = resolveReceiverType(role);
        Long receiverId = resolveReceiverId(accountId, receiverType);

        return notificationRepository
                .findUnreadNotifications(
                        receiverType,
                        receiverId,
                        pageable
                )
                .map(NotificationRes::new);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long accountId, Role role) {
        NotificationReceiverType receiverType = resolveReceiverType(role);
        Long receiverId = resolveReceiverId(accountId, receiverType);

        return notificationRepository.countUnreadByUserId(receiverId);
    }


    private void sendIfConnected(Notification notification) {
        if (notification.getReceiverType() == NotificationReceiverType.USER) {
            sseEmitterManager.sendToUser(
                    notification.getReceiverId(),
                    notification
            );
        } else {
            sseEmitterManager.sendToShop(
                    notification.getReceiverId(),
                    notification
            );
        }
    }
    private Long resolveReceiverId(Long accountId, NotificationReceiverType type) {
        if (type == NotificationReceiverType.USER) {
            return userRepository.findIdByAccountId(accountId)
                    .orElseThrow(() ->
                            new AccessDeniedException("USER 계정이 아닙니다.")
                    );
        }

        if (type == NotificationReceiverType.SHOP) {
            return shopRepository.findIdByAccountId(accountId)
                    .orElseThrow(() ->
                            new AccessDeniedException("SHOP 계정이 아닙니다.")
                    );
        }

        throw new CustomException(ErrorCode.INVALID_RECEIVER_TYPE);
    }

    private NotificationReceiverType resolveReceiverType(Role role) {
        if (role == Role.ROLE_USER) {
            return NotificationReceiverType.USER;
        }
        if (role == Role.ROLE_SHOP) {
            return NotificationReceiverType.SHOP;
        }
        throw new CustomException(ErrorCode.INVALID_RECEIVER_TYPE);
    }

    public void notifyShopOrderRequested(OrderRequest order, Long receiverId, NotificationType type, String content) {
        createNotification(
            new NotificationCreateReq(
                    NotificationReceiverType.SHOP,
                    receiverId,
                    type,
                    order.getOrderNumber() + " : " + type.getTitle(),
                    content,
                    order.getId()
            )
        );
    }
}
