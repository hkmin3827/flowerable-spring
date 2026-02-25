package com.flowerable.spring.jwt;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Getter @Setter
public class JwtProperties {
    private String secret;

    // Access Token 만료 (15분)
    private long accessExpiration;

    // Refresh Token 만료 (14일)
    private long refreshExpiration;
}
