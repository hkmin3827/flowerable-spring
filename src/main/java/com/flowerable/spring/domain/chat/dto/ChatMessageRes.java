package com.flowerable.spring.domain.chat.dto;

import com.flowerable.spring.domain.chat.constant.SenderType;
import com.flowerable.spring.domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageRes {
    private final Long id;
    private final Long senderId;
    private final SenderType senderType;
    private final String content;
    private final LocalDateTime sentAt;
    private final Boolean isRead;

    public static ChatMessageRes from(ChatMessage message) {
        return ChatMessageRes.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .build();
    }
}