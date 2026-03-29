package com.flowerable.spring.application.chat.dto;

public record ChatMessageSendReq(
        Long chatRoomId,
        String content
) {}