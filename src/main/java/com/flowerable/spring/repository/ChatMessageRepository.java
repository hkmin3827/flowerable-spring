package com.flowerable.spring.repository;

import com.flowerable.spring.constant.chat.SenderType;
import com.flowerable.spring.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);

    @Modifying
    @Query("""
        update ChatMessage m
        set m.isRead = true
        where m.chatRoom.id = :chatRoomId
          and m.senderType <> :mySenderType
          and m.isRead = false
    """)
    int markMessagesAsRead(
            Long chatRoomId,
            SenderType mySenderType
    );
}
