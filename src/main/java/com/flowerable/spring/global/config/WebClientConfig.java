package com.flowerable.spring.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient kakaoOAuth2WebClient(
            WebClient.Builder builder,
            @Value("${KAKAO_ADMIN_KEY") String adminKey
    ){
        return builder
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader("Authorization","KakaoAK"+adminKey)
                .build();
    }

    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer ->
                        configurer.defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024) // 10MB
                )
                .build();
    }

    @Bean
    public WebClient tossClient(@Value("${toss.secret-key}") String secretKey) {
        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64.getEncoder()
                                .encodeToString((secretKey + ":").getBytes()))
                .build();
    }
}
