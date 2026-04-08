package com.flowerable.spring.application.ai;

import com.flowerable.spring.application.ai.dto.FlowerRecommendRequest;
import com.flowerable.spring.application.ai.dto.FlowerRecommendResponse;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecommendService {

    private static final Duration AI_TIMEOUT = Duration.ofSeconds(120);

    private final WebClient chatBotWebClient;

    public FlowerRecommendResponse recommend(FlowerRecommendRequest req) {
        log.info("[AI] 꽃 추천 요청 - purpose='{}', location='{}'",
                req.purpose(), req.location());

        return chatBotWebClient.post()
                .uri("/recommend")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(FlowerRecommendResponse.class)
                .timeout(AI_TIMEOUT)
                .onErrorMap(TimeoutException.class, ex -> {
                    log.error("[AI] 응답 타임아웃 ({}s 초과)", AI_TIMEOUT.getSeconds());
                    return new CustomException(ErrorCode.AI_SERVER_TIMEOUT);
                })
                .onErrorMap(WebClientResponseException.class, ex -> {
                    log.error("[AI] 서버 오류 - status={}, body={}",
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                    return new CustomException(ErrorCode.AI_SERVER_ERROR);
                })
                .onErrorMap(ex -> !(ex instanceof CustomException), ex -> {
                    log.error("[AI] 예상치 못한 오류", ex);
                    return new CustomException(ErrorCode.AI_SERVER_ERROR);
                })
                .blockOptional()
                .orElseThrow(() -> new CustomException(ErrorCode.AI_SERVER_ERROR));
    }


    public Mono<FlowerRecommendResponse> recommendAsync(FlowerRecommendRequest req) {
        return chatBotWebClient.post()
                .uri("/recommend")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(FlowerRecommendResponse.class)
                .timeout(AI_TIMEOUT)
                .onErrorMap(TimeoutException.class,
                        ex -> new CustomException(ErrorCode.AI_SERVER_TIMEOUT))
                .onErrorMap(WebClientResponseException.class,
                        ex -> new CustomException(ErrorCode.AI_SERVER_ERROR))
                .onErrorMap(ex -> !(ex instanceof CustomException),
                        ex -> new CustomException(ErrorCode.AI_SERVER_ERROR));
    }
}
