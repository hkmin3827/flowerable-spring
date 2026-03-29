package com.flowerable.spring.application.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomListRes(
        Long id,
        Long userId,
        Long shopId,
        String opponentName,
        String lastMessage,
        LocalDateTime lastMessageAt,
        Long unreadCount
) {}