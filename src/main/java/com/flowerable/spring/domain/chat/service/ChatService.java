package com.flowerable.spring.service.chat;


import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.constant.chat.SenderType;
import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.notification.NotificationReceiverType;
import com.flowerable.spring.constant.notification.NotificationType;
import com.flowerable.spring.dto.chat.ChatMessageRes;
import com.flowerable.spring.dto.chat.ChatMessageSendReq;
import com.flowerable.spring.dto.chat.ChatRoomListRes;
import com.flowerable.spring.dto.chat.ChatRoomRes;
import com.flowerable.spring.dto.notification.NotificationCreateReq;
import com.flowerable.spring.entity.chat.ChatMessage;
import com.flowerable.spring.entity.chat.ChatRoom;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.entity.user.User;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.repository.*;
import com.flowerable.spring.service.notification.NotificationService;
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
        // 채팅방 존재 확인
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        Long receiverId = resolveReceiverId(accountId, role);

        if (role == Role.ROLE_USER && !chatRoom.getUserId().equals(receiverId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        if (role == Role.ROLE_SHOP && !chatRoom.getShopId().equals(receiverId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        // 메시지 목록 조회
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomId(chatRoomId);

        // 상대방 메시지 읽음 처리
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
                role == Role.ROLE_USER ? shopId : userId,
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