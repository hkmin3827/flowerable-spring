package com.flowerable.spring.jwt;


import com.flowerable.spring.constant.Role;
import com.flowerable.spring.constant.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtProvider(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }


    // USER, SHOP 공용 Token 발급
    public String createToken(Long id, TokenType tokenType, Role role) {
        long now = System.currentTimeMillis();

        var builder = Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("type", tokenType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(now + jwtProperties.getExpiration()));

        if (tokenType == TokenType.USER && role != null) {
            builder.claim("role", role.name());
        }

        return Jwts.builder()
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

    public TokenType getTokenType(String token){
        String type = parseClaims(token).get("type", String.class);
        return TokenType.valueOf(type);
    }

    // type = "USER"일 시 ROLE = "ROLE_USER" 또는 "ROLE_ADMIN"
    public Role getRole(String token) {
        Claims claims = parseClaims(token);
        String role = claims.get("role", String.class);
        return role != null ? Role.valueOf(role) : null;
    }
}
