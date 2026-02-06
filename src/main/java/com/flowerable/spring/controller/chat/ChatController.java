package com.flowerable.spring.controller.chat;

import com.flowerable.spring.dto.chat.ChatMessageSendReq;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public ResponseEntity<Void> send(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatMessageSendReq req) {

        chatService.sendMessage(
                userDetails.getId(),
                userDetails.getRole(),
                req
        );
        log.info("[WS] @MessageMapping entered, payload={}", req);
        return ResponseEntity.noContent().build();
    }
}
