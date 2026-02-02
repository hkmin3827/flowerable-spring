package com.flowerable.spring.controller.order;

import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.infra.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// 로그인 시 호출 -> sse 연결 실행
// 로그아웃 시 react에서 eventSource.close(); -> 연결 끊음
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final SseEmitterManager sseEmitterManager;
    
    @GetMapping("/subscribe/user")
    public SseEmitter subscribeUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return sseEmitterManager.connectUserByAccountId(userDetails.getId());
    }

    @GetMapping("/subscribe/shop")
    public SseEmitter subscribeShop(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return sseEmitterManager.connectShopByAccountId(userDetails.getId());
    }
}