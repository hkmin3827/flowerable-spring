package com.flowerable.spring.service.chat;


import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.constant.chat.SenderType;
import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.notification.NotificationReceiverType;
import com.flowerable.spring.constant.notification.NotificationType;
import com.flowerable.spring.dto.chat.ChatMessageRes;
import com.flowerable.spring.dto.chat.ChatMessageSendReq;
import com.flowerable.spring.dto.notification.NotificationCreateReq;
import com.flowerable.spring.entity.chat.ChatMessage;
import com.flowerable.spring.entity.chat.ChatRoom;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.entity.user.User;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.repository.ChatMessageRepository;
import com.flowerable.spring.repository.ChatRoomRepository;
import com.flowerable.spring.repository.ShopRepository;
import com.flowerable.spring.repository.UserRepository;
import com.flowerable.spring.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Transactional
    public void sendMessage(
            Long accountId,
            Role senderRole,
            ChatMessageSendReq req
    ) {
        SenderContext sender = resolveSender(accountId, senderRole, req.targetId());


        ChatRoom chatRoom = chatRoomRepository
                .findByUserIdAndShopId(sender.userId(), sender.shopId())
                .orElseGet(() ->
                        chatRoomRepository.save(
                                ChatRoom.create(sender.userId(), sender.shopId())
                        )
                );

        ChatMessage message = ChatMessage.create(
                sender.senderId(),
                sender.senderType(),
                req.content()
        );

        chatRoom.addMessage(message);
        chatMessageRepository.save(message);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + chatRoom.getId(),
                ChatMessageRes.from(message)
        );

        notificationService.createNotification(
                new NotificationCreateReq(
                        sender.receiverType(),
                        sender.receiverId(),
                        NotificationType.MESSAGE_RECEIVED,
                        NotificationType.MESSAGE_RECEIVED.getTitle(),
                        message.getContent(),
                        chatRoom.getId()
                )
        );

        log.info(
                "[CHAT] sendMessage roomId={}, senderType={}, senderId={}",
                chatRoom.getId(),
                sender.senderType(),
                sender.senderId()
        );
    }

//    @Transactional
//    public void enterChatRoom(
//            Long chatRoomId,
//            Long accountId,
//            Role role
//    ) {
//        Long receiverId = resolveReceiverId(accountId, role);
//
//        notificationService.markChatRoomNotificationsAsRead(
//                chatRoomId,
//                receiverId
//        );
//    }

    private SenderContext resolveSender(
            Long accountId,
            Role role,
            Long targetId
    ) {
        if (role == Role.ROLE_USER) {
            User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                    .orElseThrow(UserNotFoundException::new);

            return SenderContext.user(
                    user.getId(),
                    targetId
            );
        }

        if (role == Role.ROLE_SHOP) {
            Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                    .orElseThrow(ShopNotFoundException::new);

            return SenderContext.shop(
                    targetId,
                    shop.getId()
            );
        }

        throw new CustomException(ErrorCode.INVALID_ROLE);
    }


    private Long resolveReceiverId(Long accountId, Role role) {
        if (role == Role.ROLE_USER) {
            return userRepository.findIdByAccountId(accountId)
                    .orElseThrow(UserNotFoundException::new);
        }
        if (role == Role.ROLE_SHOP) {
            return shopRepository.findIdByAccountId(accountId)
                    .orElseThrow(ShopNotFoundException::new);
        }
        throw new CustomException(ErrorCode.INVALID_ROLE);
    }

    private record SenderContext(
            Long userId,
            Long shopId,
            Long senderId,
            SenderType senderType,
            Long receiverId,
            NotificationReceiverType receiverType
    ) {
        static SenderContext user(Long userId, Long shopId) {
            return new SenderContext(
                    userId,
                    shopId,
                    userId,
                    SenderType.USER,
                    shopId,
                    NotificationReceiverType.SHOP
            );
        }

        static SenderContext shop(Long userId, Long shopId) {
            return new SenderContext(
                    userId,
                    shopId,
                    shopId,
                    SenderType.SHOP,
                    userId,
                    NotificationReceiverType.USER
            );
        }
    }
}