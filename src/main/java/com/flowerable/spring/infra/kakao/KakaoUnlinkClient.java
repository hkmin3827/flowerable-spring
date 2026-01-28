package com.flowerable.spring.infra.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KakaoUnlinkClient {
    private final WebClient kakaoOAuth2WebClient;

    public void unlink(String kakaoUserId){
        kakaoOAuth2WebClient.post()
                .uri("/v1/user/unlink")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("target_id_type=user_id&target_id=" + kakaoUserId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
