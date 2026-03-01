package com.flowerable.spring.domain.chat.dto;

public record ChatMessageSendReq(
        Long chatRoomId,
        String content
) {}