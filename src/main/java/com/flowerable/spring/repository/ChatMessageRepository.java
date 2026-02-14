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

    /**
     * 채팅방 ID로 메시지 목록 조회 (최근순)
     */
    @Query("""
        SELECT cm FROM ChatMessage cm
        WHERE cm.chatRoom.id = :chatRoomId
        ORDER BY cm.sentAt ASC
    """)
    List<ChatMessage> findByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    /**
     * 채팅방의 상대방 메시지 읽음 처리
     * (USER가 읽을 때는 SHOP 메시지를, SHOP이 읽을 때는 USER 메시지를 읽음 처리)
     */
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
