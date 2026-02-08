package com.flowerable.spring.repository;

import com.flowerable.spring.constant.chat.SenderType;
import com.flowerable.spring.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ChatMessage m
        set m.isRead = true
        where m.chatRoom.id = :chatRoomId
          and m.isRead = false
          and m.senderId <> :readerId
    """)
    int markMessagesAsRead(
            @Param("chatRoomId") Long chatRoomId,
            @Param("readerId") Long readerId
    );
}
