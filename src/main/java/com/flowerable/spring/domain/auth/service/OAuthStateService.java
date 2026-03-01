package com.flowerable.spring.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OAuthStateService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String OAUTH_CODE_PREFIX = "oauth:code:";
    private static final long EXPIRATION_MINUTES = 5;

    // OAuth 인증 코드 생성 및 저장
    public String createAuthCode(String provider, String providerId) {
        String code = UUID.randomUUID().toString();
        String key = OAUTH_CODE_PREFIX + code;
        String value = provider + ":" + providerId;

        redisTemplate.opsForValue().set(
                key,
                value,
                EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        return code;
    }

     // 인증 코드 검증 및 정보 반환
    public String[] validateAndConsumeCode(String code) {
        String key = OAUTH_CODE_PREFIX + code;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        redisTemplate.delete(key);

        return value.split(":");
    }
}