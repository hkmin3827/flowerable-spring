package com.flowerable.spring.domain.chat.repository;

import com.flowerable.spring.domain.chat.dto.ChatRoomListRes;
import com.flowerable.spring.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserIdAndShopId(Long userId, Long shopId);

    @Query("""
    select new com.flowerable.spring.domain.chat.dto.ChatRoomListRes(
        cr.id,
        cr.userId,
        cr.shopId,
        s.shopName,
        cr.lastMessage,
        cr.lastMessageAt,
        (
            select count(cm)
            from ChatMessage cm
            where cm.chatRoom.id = cr.id
              and cm.senderType = com.flowerable.spring.domain.chat.constant.SenderType.SHOP
              and cm.isRead = false
        )
    )
    from ChatRoom cr
    join Shop s on s.id = cr.shopId
    where cr.userId = :userId
    order by cr.lastMessageAt desc
""")
    List<ChatRoomListRes> findChatRoomsByUserId(Long userId);

    @Query("""
    select new com.flowerable.spring.domain.chat.dto.ChatRoomListRes(
        cr.id,
        cr.userId,
        cr.shopId,
        u.name,
        cr.lastMessage,
        cr.lastMessageAt,
        (
            select count(cm)
            from ChatMessage cm
            where cm.chatRoom.id = cr.id
              and cm.senderType = com.flowerable.spring.domain.chat.constant.SenderType.USER
              and cm.isRead = false
        )
    )
    from ChatRoom cr
    join User u on u.id = cr.userId
    where cr.shopId = :shopId
    order by cr.lastMessageAt desc
""")
    List<ChatRoomListRes> findChatRoomsByShopId(Long shopId);
}
