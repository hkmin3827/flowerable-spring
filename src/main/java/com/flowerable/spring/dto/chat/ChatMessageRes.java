package com.flowerable.spring.dto.chat;

import com.flowerable.spring.constant.chat.SenderType;
import com.flowerable.spring.entity.chat.ChatMessage;
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

    public static ChatMessageRes from(ChatMessage message) {
        return ChatMessageRes.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }
}