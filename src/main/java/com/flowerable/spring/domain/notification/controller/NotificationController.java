package com.flowerable.spring.controller.notification;

import com.flowerable.spring.dto.notification.NotificationRes;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.infra.sse.SseEmitterManager;
import com.flowerable.spring.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// 로그인 시 호출 -> sse 연결 실행
// 로그아웃 시 react에서 eventSource.close(); -> 연결 끊음
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

    /**
     * 안 읽은 알림 조회 (최신순)
     */
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

    /**
     * 알림 읽음 처리 (알림 클릭 시 호출)
     */
    @PatchMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(notificationId, userDetails.getId());
    }
}