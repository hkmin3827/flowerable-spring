package com.flowerable.spring.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMillis;

    /** =====================
     *  Redis Key Prefix
     *  ===================== */
    private static final String REFRESH_TOKEN_PREFIX = "RT:"; // RT:{userId}
    private static final String BLACKLIST_PREFIX = "BL:";     // BL:{jti}

    /** =====================
     *  Refresh Token
     *  ===================== */

    /**
     * Refresh Token 저장
     * Key   : RT:{userId}
     * Value : refreshToken (String)
     */
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                refreshTokenExpirationMillis,
                TimeUnit.MILLISECONDS
        );

        log.debug("Saved refresh token. userId={}", userId);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(key);

        return value != null ? value.toString() : null;
    }

    /**
     * Refresh Token 검증
     */
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String storedToken = getRefreshToken(userId);

        if (storedToken == null) {
            log.warn("No refresh token found. userId={}", userId);
            return false;
        }

        return Objects.equals(storedToken, refreshToken);
    }

    /**
     * Refresh Token 삭제 (로그아웃 / 재발급 시)
     */
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        log.debug("Deleted refresh token. userId={}", userId);
    }

    /** =====================
     *  Access Token Blacklist
     *  ===================== */

    /**
     * Access Token 블랙리스트 등록
     * Key   : BL:{jti}
     * Value : true
     */
    public void addAccessTokenToBlacklist(String jti, long expirationMillis) {
        String key = BLACKLIST_PREFIX + jti;

        redisTemplate.opsForValue().set(
                key,
                Boolean.TRUE,
                expirationMillis,
                TimeUnit.MILLISECONDS
        );

        log.debug("Access token blacklisted. jti={}", jti);
    }

    /**
     * Access Token 블랙리스트 여부 확인
     */
    public boolean isAccessTokenBlacklisted(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /** =====================
     *  Utility
     *  ===================== */

    /**
     * Redis에 Refresh Token이 존재하는지 여부
     */
    public boolean hasRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}