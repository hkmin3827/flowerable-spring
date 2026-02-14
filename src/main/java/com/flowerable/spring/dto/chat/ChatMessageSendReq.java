package com.flowerable.spring.dto.chat;

import com.flowerable.spring.constant.chat.SenderType;

public record ChatMessageSendReq(
        Long chatRoomId,
        String content
) {}