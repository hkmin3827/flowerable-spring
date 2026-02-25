package com.flowerable.spring.entity.chat;


import com.flowerable.spring.constant.chat.SenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType senderType;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    public void assignRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public static ChatMessage create(Long senderId, SenderType senderType, String content) {
        ChatMessage message = new ChatMessage();
        message.senderId = senderId;
        message.senderType = senderType;
        message.content = content;
        return message;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}
