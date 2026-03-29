package com.flowerable.spring.application.chat;


import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.domain.chat.SenderType;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.notification.NotificationReceiverType;
import com.flowerable.spring.domain.notification.NotificationType;
import com.flowerable.spring.application.chat.dto.ChatMessageRes;
import com.flowerable.spring.application.chat.dto.ChatMessageSendReq;
import com.flowerable.spring.application.chat.dto.ChatRoomListRes;
import com.flowerable.spring.application.chat.dto.ChatRoomRes;
import com.flowerable.spring.domain.chat.ChatMessageRepository;
import com.flowerable.spring.domain.chat.ChatRoomRepository;
import com.flowerable.spring.application.notification.dto.NotificationCreateReq;
import com.flowerable.spring.domain.chat.ChatMessage;
import com.flowerable.spring.domain.chat.ChatRoom;
import com.flowerable.spring.domain.notification.NotificationRepository;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.domain.shop.ShopRepository;
import com.flowerable.spring.domain.user.User;
import com.flowerable.spring.domain.user.UserRepository;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.ShopNotFoundException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import com.flowerable.spring.application.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomListRes> getChatRooms(
            Long accountId,
            Role role
    ) {
        if (role == Role.ROLE_USER) {
            Long userId = userRepository.findIdByAccountId(accountId)
                    .orElseThrow(UserNotFoundException::new);

            return chatRoomRepository.findChatRoomsByUserId(userId);
        }

        if (role == Role.ROLE_SHOP) {
            Long shopId = shopRepository.findIdByAccountId(accountId)
                    .orElseThrow(ShopNotFoundException::new);

            return chatRoomRepository.findChatRoomsByShopId(shopId);
        }

        throw new CustomException(ErrorCode.INVALID_ROLE);
    }
    @Transactional
    public void sendMessage(
            Long accountId,
            Role role,
            ChatMessageSendReq req
    ) {
        ChatRoom chatRoom = chatRoomRepository.findById(req.chatRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        SenderType senderType = SenderType.USER;
        Long receiverId;
        NotificationReceiverType receiverType;

        if (role == Role.ROLE_USER) {
            Long userId = userRepository.findIdByAccountId(accountId)
                    .orElseThrow(UserNotFoundException::new);

            if (!chatRoom.getUserId().equals(userId)) {
                throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
            }

            senderType = senderType.USER;
            receiverId = chatRoom.getShopId();
            receiverType = NotificationReceiverType.SHOP;

        } else if (role == Role.ROLE_SHOP) {

            Long shopId = shopRepository.findIdByAccountId(accountId)
                    .orElseThrow(ShopNotFoundException::new);

            if (!chatRoom.getShopId().equals(shopId)) {
                throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
            }

            senderType = senderType.SHOP;
            receiverId = chatRoom.getUserId();
            receiverType = NotificationReceiverType.USER;
        } else {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }

        ChatMessage message = ChatMessage.create(
                chatRoom.getId(),
                senderType,
                req.content()
        );

        chatRoom.addMessage(message);

        chatMessageRepository.save(message);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + chatRoom.getId(),
                ChatMessageRes.from(message)
        );

        notificationService.createOrUpdateChatNotification(
                new NotificationCreateReq(
                        receiverType,
                        receiverId,
                        NotificationType.MESSAGE_RECEIVED,
                        NotificationType.MESSAGE_RECEIVED.getTitle(),
                        "새 메세지가 도착했습니다.",
                        chatRoom.getId()
                )
        );
    }

    @Transactional
    public List<ChatMessageRes> getChatMessages(Long chatRoomId, Long accountId, Role role) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        Long receiverId = resolveReceiverId(accountId, role);

        if (role == Role.ROLE_USER && !chatRoom.getUserId().equals(receiverId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        if (role == Role.ROLE_SHOP && !chatRoom.getShopId().equals(receiverId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomId(chatRoomId);

        SenderType opponentSender = role.equals("ROLE_USER")
                ? SenderType.SHOP
                : SenderType.USER;

        chatMessageRepository.markMessagesAsRead(chatRoomId, opponentSender);

        return messages.stream()
                .map(ChatMessageRes::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomRes enterChatRoom(
            Long accountId,
            Role role,
            Long targetId
    ) {
        Long userId;
        Long shopId;
        String opponentName;
        String opponentTelnum;

        if (role == Role.ROLE_USER) {

            userId = userRepository.findIdByAccountId(accountId)
                    .orElseThrow(UserNotFoundException::new);

            shopId = targetId;
            Shop shop = shopRepository.findDetailById(shopId)
                    .orElseThrow(ShopNotFoundException::new);
            opponentName = shop.getShopName();
            opponentTelnum = shop.getAccount().getTelnum();

        } else if (role == Role.ROLE_SHOP) {

            shopId = shopRepository.findIdByAccountId(accountId)
                    .orElseThrow(ShopNotFoundException::new);

            userId = targetId;
            User user = userRepository.findDetailById(userId)
                    .orElseThrow(UserNotFoundException::new);
            opponentName = user.getName();
            opponentTelnum = user.getAccount().getTelnum();

        } else {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }


        ChatRoom chatRoom = chatRoomRepository
                .findByUserIdAndShopId(userId, shopId)
                .orElseGet(() ->
                        chatRoomRepository.save(
                                ChatRoom.create(userId, shopId)
                        )
                );

        if (role == Role.ROLE_USER && !chatRoom.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        if (role == Role.ROLE_SHOP && !chatRoom.getShopId().equals(shopId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        SenderType opponentSender =
                role == Role.ROLE_USER ? SenderType.SHOP : SenderType.USER;

        chatMessageRepository.markMessagesAsRead(
                chatRoom.getId(),
                opponentSender
        );

        notificationRepository.markAsReadByTypeAndReceiverIdAndReferenceId(
                NotificationType.MESSAGE_RECEIVED,
                role == Role.ROLE_USER ? userId : shopId,
                chatRoom.getId()
        );

        return ChatRoomRes.from(chatRoom, opponentName, opponentTelnum);
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

}