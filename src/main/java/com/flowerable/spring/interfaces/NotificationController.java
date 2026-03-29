package com.flowerable.spring.interfaces;

import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.application.notification.dto.NotificationRes;
import com.flowerable.spring.global.jwt.JwtProvider;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.infra.sse.SseEmitterManager;
import com.flowerable.spring.application.notification.NotificationService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final SseEmitterManager sseEmitterManager;
    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;
    

    @GetMapping("/subscribe/user")
    public SseEmitter subscribeUser(@RequestParam("token") String token) {
        try {
            Long accountId = jwtProvider.getId(token);
            return sseEmitterManager.connectUserByAccountId(accountId);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "SSE Token Expired");
        }

    }

    @GetMapping("/subscribe/shop")
    public SseEmitter subscribeShop(@RequestParam("token") String token) {
        try{
            Long accountId = jwtProvider.getId(token);
            return sseEmitterManager.connectShopByAccountId(accountId);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "SSE Token Expired");
        }
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Void> disconnect(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long accountId = userDetails.getId();
        Role role = userDetails.getRole();

        if (role == Role.ROLE_SHOP) {
            sseEmitterManager.disconnectShop(accountId);
        } else {
            sseEmitterManager.disconnectUser(accountId);
        }

        return ResponseEntity.ok().build();
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