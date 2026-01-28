package com.flowerable.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

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
    public WebClient kakaoMapWebClient(
            WebClient.Builder builder,
            @Value(("${KAKAO_MAP_REST_API_KEY")) String apiKey
    ){
        return builder
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization","KakaoAK" + apiKey)
                .build();
    }

}
