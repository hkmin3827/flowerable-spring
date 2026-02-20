package com.flowerable.spring.jwt;

import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.constant.auth.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtProvider(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }


    public String createAccessToken(Long accountId, Role role) {


        return createToken(
                accountId,
                TokenType.ACCESS,
                role,
                jwtProperties.getAccessExpiration()
        );
    }

    public String createPasswordResetToken(Long accountId, Role role, Duration duration) {
        return createToken(
                accountId,
                TokenType.PASSWORD_RESET,
                role,
                duration.toMillis()
        );
    }

    public String createRefreshToken(Long accountId,Role role) {
        return createToken(
                accountId,
                TokenType.REFRESH,
                role,
                jwtProperties.getRefreshExpiration()
        );
    }

    // USER, SHOP 공용 Token 발급
    public String createToken(
            Long accountId,
            TokenType tokenType,
            Role role,
            long expiration
            ) {
        long now = System.currentTimeMillis();

        var builder = Jwts.builder()
                .setId(java.util.UUID.randomUUID().toString())
                .setSubject(String.valueOf(accountId))
                .claim("role", role.name())
                .claim("tokenType", tokenType.name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration));

        return builder
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }


    public TokenType getTokenType(String token) {
        return TokenType.valueOf(
                parseClaims(token).get("tokenType", String.class)
        );
    }

    public Role getRole(String token) {
        return Role.valueOf(parseClaims(token).get("role", String.class));
    }

    // Redis 블랙리스트
    public String getJti(String token) {
        return parseClaims(token).getId();
    }
}
