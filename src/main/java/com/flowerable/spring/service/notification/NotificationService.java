package com.flowerable.spring.service.notification;

import com.flowerable.spring.constant.NotificationReceiverType;
import com.flowerable.spring.constant.NotificationType;
import com.flowerable.spring.dto.notification.NotificationCreateReq;
import com.flowerable.spring.entity.notification.Notification;
import com.flowerable.spring.exception.NotificationNotFoundException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.infra.sse.SseEmitterManager;
import com.flowerable.spring.repository.NotificationRepository;
import com.flowerable.spring.repository.ShopRepository;
import com.flowerable.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    // 로그인 시 sse로 실시간 전송
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
    public void markAsRead(Long notificationId, Long accountId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (notification.getReceiverType() == NotificationReceiverType.USER){
            Long userId = userRepository.findIdByAccountId(accountId)
                    .orElseThrow(UserNotFoundException::new);

            if (!notification.getReceiverId().equals(userId)) {
                throw new AccessDeniedException("알림 수신자와 계정이 일치하지 않습니다.");
            }
        }

        if (notification.getReceiverType() == NotificationReceiverType.SHOP){
            Long shopId = shopRepository.findIdByAccountId(accountId)
                    .orElseThrow(UserNotFoundException::new);

            if (!notification.getReceiverId().equals(shopId)) {
                throw new AccessDeniedException("알림 수신자와 계정이 일치하지 않습니다.");
            }
        }

        notification.markAsRead();
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
}
