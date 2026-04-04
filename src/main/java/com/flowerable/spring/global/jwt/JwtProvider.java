package com.flowerable.spring.global.jwt;

import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.domain.auth.constant.TokenType;
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

    public String createAccessToken(Long accountId, Role role, String email) {
        return createToken(
                accountId,
                TokenType.ACCESS,
                role,
                email,
                jwtProperties.getAccessExpiration()
        );
    }

    public String createPasswordResetToken(Long accountId, Role role, String email, Duration duration) {
        return createToken(
                accountId,
                TokenType.PASSWORD_RESET,
                role,
                email,
                duration.toMillis()
        );
    }

    public String createRefreshToken(Long accountId,Role role, String email) {
        return createToken(
                accountId,
                TokenType.REFRESH,
                role,
                email,
                jwtProperties.getRefreshExpiration()
        );
    }

    public String createToken(
            Long accountId,
            TokenType tokenType,
            Role role,
            String email,
            long expiration
            ) {
        long now = System.currentTimeMillis();

        var builder = Jwts.builder()
                .setId(java.util.UUID.randomUUID().toString())
                .setSubject(String.valueOf(accountId))
                .claim("role", role.name())
                .claim("tokenType", tokenType.name())
                .claim("email", email)
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
    public String getEmail(String token) { return parseClaims(token).get("email", String.class); }

    public Role getRole(String token) {
        return Role.valueOf(parseClaims(token).get("role", String.class));
    }

    public String getJti(String token) {
        return parseClaims(token).getId();
    }
}
