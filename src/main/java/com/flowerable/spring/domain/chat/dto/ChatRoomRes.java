package com.flowerable.spring.domain.chat.dto;

import com.flowerable.spring.domain.chat.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class ChatRoomRes {
    private final Long id;
    private final String opponentName;
    private final String telnum;
    private final Long userId;
    private final Long shopId;

    public static ChatRoomRes from(ChatRoom chatRoom, String opponentName, String telnum) {
        return ChatRoomRes.builder()
                .id(chatRoom.getId())
                .opponentName(opponentName)
                .telnum(telnum)
                .userId(chatRoom.getUserId())
                .shopId(chatRoom.getShopId())
                .build();
    }
}
