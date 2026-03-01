package com.flowerable.spring.domain.chat.repository;

import com.flowerable.spring.domain.chat.constant.SenderType;
import com.flowerable.spring.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
        SELECT cm FROM ChatMessage cm
        WHERE cm.chatRoom.id = :chatRoomId
        ORDER BY cm.sentAt ASC
    """)
    List<ChatMessage> findByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Modifying
    @Query("""
        UPDATE ChatMessage cm
        SET cm.isRead = true
        WHERE cm.chatRoom.id = :chatRoomId
        AND cm.senderType = :senderType
        AND cm.isRead = false
    """)
    void markMessagesAsRead(
            @Param("chatRoomId") Long chatRoomId,
            @Param("senderType") SenderType senderType
    );
}
