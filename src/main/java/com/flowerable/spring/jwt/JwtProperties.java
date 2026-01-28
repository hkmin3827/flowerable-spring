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

    // 15분
    private long expiration;
}
