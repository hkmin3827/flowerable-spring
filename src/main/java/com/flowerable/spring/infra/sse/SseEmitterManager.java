package com.flowerable.spring.infra.sse;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.repository.ShopRepository;
import com.flowerable.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private static final long TIMEOUT = 60L * 60 * 1000; // 1시간

    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final Map<Long, SseEmitter> shopEmitters = new ConcurrentHashMap<>();

    public SseEmitter connectUserByAccountId(Long accountId) {
        Long userId = userRepository.findIdByAccountId(accountId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROLE_NOT_USER));

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        userEmitters.put(userId, emitter);
        removeOnComplete(emitter, userId, userEmitters);
        return emitter;
    }

    public SseEmitter connectShopByAccountId(Long accountId) {
        Long shopId = shopRepository.findIdByAccountId(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_SHOP));

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        shopEmitters.put(shopId, emitter);

        log.info("[SSE] shop emitter connected. shopId={}", shopId);
        emitter.onCompletion(() -> {
            shopEmitters.remove(shopId);
            log.info("[SSE] shop emitter completed. shopId={}", shopId);
        });

        emitter.onTimeout(() -> {
            shopEmitters.remove(shopId);
            log.info("[SSE] shop emitter timeout. shopId={}", shopId);
        });

        emitter.onError(e -> {
            shopEmitters.remove(shopId);
            log.warn("[SSE] shop emitter error. shopId={}", shopId, e);
        });
//        removeOnComplete(emitter, shopId, shopEmitters);
        return emitter;
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
                    .name("order")
                    .data(data));
        } catch (Exception e) {
            emitters.remove(id);
        }
    }

    private void removeOnComplete(
            SseEmitter emitter,
            Long id,
            Map<Long, SseEmitter> map
    ) {
        emitter.onCompletion(() -> map.remove(id));
        emitter.onTimeout(() -> map.remove(id));
        emitter.onError(e -> map.remove(id));
    }
}