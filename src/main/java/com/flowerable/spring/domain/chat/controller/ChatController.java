package com.flowerable.spring.controller.chat;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.dto.chat.ChatMessageRes;
import com.flowerable.spring.dto.chat.ChatMessageSendReq;
import com.flowerable.spring.dto.chat.ChatRoomListRes;
import com.flowerable.spring.dto.chat.ChatRoomRes;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageRes>> getChatMessages(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ChatMessageRes> messages = chatService.getChatMessages(chatRoomId, userDetails.getId(), userDetails.getRole());
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/chat/message")
    public ResponseEntity<Void> send(
            Principal principal,
            @RequestBody ChatMessageSendReq req) {

        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) principal;

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        chatService.sendMessage(
                userDetails.getId(),
                userDetails.getRole(),
                req
        );
        log.info("[WS] @MessageMapping entered, payload={}", req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms")
    public List<ChatRoomListRes> getChatRooms(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return chatService.getChatRooms(
                user.getId(),
                user.getRole()
        );
    }
    @PostMapping("/chat-room/{targetId}")
    public ResponseEntity<ChatRoomRes> enterChatRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long targetId
    )
    {
        ChatRoomRes res = chatService.enterChatRoom(userDetails.getId(), userDetails.getRole(), targetId);
        return ResponseEntity.ok(res);
    }
}
