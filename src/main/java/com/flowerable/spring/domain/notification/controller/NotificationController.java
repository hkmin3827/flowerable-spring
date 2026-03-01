package com.flowerable.spring.domain.notification.controller;

import com.flowerable.spring.domain.notification.dto.NotificationRes;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.infra.sse.SseEmitterManager;
import com.flowerable.spring.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final SseEmitterManager sseEmitterManager;
    private final NotificationService notificationService;
    
    @GetMapping("/subscribe/user")
    public SseEmitter subscribeUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return sseEmitterManager.connectUserByAccountId(userDetails.getId());
    }

    @GetMapping("/subscribe/shop")
    public SseEmitter subscribeShop(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return sseEmitterManager.connectShopByAccountId(userDetails.getId());
    }

    @GetMapping("/unread")
    public Page<NotificationRes> getUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        return notificationService.getUnreadNotifications(
                userDetails.getId(),
                userDetails.getRole(),
                pageable
        );
    }

    @GetMapping("/unread-count")
    public Long getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.getUnreadCount(userDetails.getId(), userDetails.getRole());
    }

    @PatchMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(notificationId, userDetails.getId());
    }
}