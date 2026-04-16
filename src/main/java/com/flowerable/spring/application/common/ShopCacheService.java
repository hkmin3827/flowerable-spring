package com.flowerable.spring.application.common;

import com.flowerable.spring.domain.shop.constant.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopCacheService {

    private static final String CACHE_KEY_PREFIX = "flowerable:shops:";

    private final RedisTemplate<String, Object> redisTemplate;


    public void evictByRegion(Region region) {
        String pattern = CACHE_KEY_PREFIX + region.name() + ":*";
        deleteByPattern(pattern);
    }

    public void evictByFlowerName(String flowerName) {
        String pattern = CACHE_KEY_PREFIX + "*:*" + flowerName + "*";
        deleteByPattern(pattern);
    }

    private void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("[ShopCache] 캐시 삭제 - 패턴: {}, 삭제 키 수: {}", pattern, keys.size());
        } else {
            log.debug("[ShopCache] 삭제할 캐시 없음 - 패턴: {}", pattern);
        }
    }
}
