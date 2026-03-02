package com.flowerable.spring.infra.sse;

import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.domain.shop.repository.ShopRepository;
import com.flowerable.spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitterManager {
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private static final long TIMEOUT = 60L * 30 * 1000; // 30분

    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final Map<Long, SseEmitter> shopEmitters = new ConcurrentHashMap<>();

    public SseEmitter connectUserByAccountId(Long accountId) {
        Long userId = userRepository.findIdByAccountId(accountId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROLE_NOT_USER));

        cleanupExistingEmitter(userId, userEmitters);

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        userEmitters.put(userId, emitter);

        registerEmitterCallbacks(emitter, userId, userEmitters);

        sendDummyData(emitter, "user-connect", userId);

        return emitter;
    }

    public SseEmitter connectShopByAccountId(Long accountId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_SHOP));

        cleanupExistingEmitter(shopId, shopEmitters);

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        shopEmitters.put(shopId, emitter);

        log.info("[SSE] shop emitter connected. shopId={}", shopId);

        registerEmitterCallbacks(emitter, shopId, shopEmitters);

        sendDummyData(emitter, "shop-connect", shopId);
        return emitter;
    }

    private void cleanupExistingEmitter(Long id, Map<Long, SseEmitter> map) {
        SseEmitter existing = map.get(id);
        if (existing != null) {
            log.info("[SSE] Cleaning up existing emitter for id={}", id);
            existing.complete();
            map.remove(id);
        }
    }

    private void sendDummyData(SseEmitter emitter, String name, Long id) {
        try {
            emitter.send(SseEmitter.event()
                    .name(name)
                    .data("connected_id:" + id));
        } catch (Exception e) {
            log.error("SSE initial send error", e);
        }
    }

    public void sendToUser(Long userId, Object data) {
        send(userEmitters, userId, data);
    }

    public void sendToShop(Long shopId, Object data) {
        send(shopEmitters, shopId, data);
    }

    private void send(Map<Long, SseEmitter> emitters, Long id, Object data) {
        SseEmitter emitter = emitters.get(id);
        if (emitter == null) return;

        try {
            log.info("[SSE] send. sendToId={}", id);
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .id(String.valueOf(System.currentTimeMillis()))
                    .data(data));
        } catch (Exception e) {
            log.warn("[SSE] send failed, removing emitter for id={}", id);
            emitters.remove(id);
        }
    }

    private void registerEmitterCallbacks(
            SseEmitter emitter,
            Long id,
            Map<Long, SseEmitter> map
    ) {
        emitter.onCompletion(() -> {
            log.info("[SSE] emitter completed. id={}", id);
            map.remove(id);
        });

        emitter.onTimeout(() -> {
            log.info("[SSE] emitter timeout. id={}", id);
            map.remove(id);
            emitter.complete();
        });

        emitter.onError(e -> {
            log.warn("[SSE] emitter error. id={}", id, e);
            map.remove(id);
            emitter.complete();
        });
    }

    @Scheduled(fixedDelay = 30_000)
    public void sendHeartbeat() {
        sendHeartbeatToEmitters(userEmitters, "user");
        sendHeartbeatToEmitters(shopEmitters, "shop");
    }

    private void sendHeartbeatToEmitters(Map<Long, SseEmitter> emitters, String type) {
        if (emitters.isEmpty()) return;

        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("ping"));
            } catch (Exception e) {
                log.debug("[SSE] heartbeat failed for {} id={}, removing.", type, id);
                emitters.remove(id);
            }
        });
    }


    public void disconnectUser(Long accountId) {
        Long userId = userRepository.findIdByAccountId(accountId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROLE_NOT_USER));

        SseEmitter emitter = userEmitters.get(userId);
        if (emitter != null) {
            emitter.complete();
            userEmitters.remove(userId);
            log.info("[SSE] User disconnected manually. userId={}", userId);
        }
    }

    public void disconnectShop(Long accountId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_SHOP));

        SseEmitter emitter = shopEmitters.get(shopId);
        if (emitter != null) {
            emitter.complete();
            shopEmitters.remove(shopId);
            log.info("[SSE] Shop disconnected manually. shopId={}", shopId);
        }
    }
}

