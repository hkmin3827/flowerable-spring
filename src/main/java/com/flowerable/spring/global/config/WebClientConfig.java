package com.flowerable.spring.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Value("${ai.server.url}")
    private String aiServerUrl;

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

    @Bean
    public WebClient chatBotWebClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder());
                    config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder());
                    // AI 응답이 길 수 있으므로 버퍼 2MB로 확장
                    config.defaultCodecs().maxInMemorySize(2 * 1024 * 1024);
                })
                .build();

        return WebClient.builder()
                .baseUrl(aiServerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(strategies)
                .build();
    }
}
