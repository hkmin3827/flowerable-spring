package com.flowerable.spring.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_rooms",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "shop_id"})
        })
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastMessageAt;

    public static ChatRoom create(Long userId, Long shopId) {
        ChatRoom room = new ChatRoom();
        room.userId = userId;
        room.shopId = shopId;
        return room;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.assignRoom(this);
        lastMessageAt = LocalDateTime.now();
    }
}