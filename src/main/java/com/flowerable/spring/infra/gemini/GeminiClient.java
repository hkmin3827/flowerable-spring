package com.flowerable.spring.infra.gemini;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;
    private final GeminiProperties geminiProperties;

    public String generateImage(String prompt) {
        String apiKey = geminiProperties.getApi().getKey();
        String model = geminiProperties.getModels().getImage();

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", prompt))
                        )
                ),
                "generationConfig", Map.of(
                        "responseModalities", List.of("IMAGE"),
                        "temperature", 1.0
                )
        );

        log.info("[Gemini] image generation request start - model: {}", model);

        JsonNode root = geminiWebClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/v1beta/models/{model}:generateContent")
                                .queryParam("key", apiKey)
                                .build(model)
                )
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(r -> log.info("[Gemini] image generation success"))
                .doOnError(e -> log.error("[Gemini] image generation failed", e))
                .block();

        return extractBase64FromImageResponse(root);
    }

    private String extractBase64FromImageResponse(JsonNode root) {
        try {
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("Gemini 응답에 candidates가 없습니다.");
            }

            JsonNode parts = candidates
                    .get(0)
                    .path("content")
                    .path("parts");

            for (JsonNode part : parts) {
                JsonNode inlineData = part.path("inlineData");
                if (!inlineData.isMissingNode()) {
                    return inlineData.path("data").asText();
                }
            }

            throw new RuntimeException("Gemini 응답에서 이미지 데이터를 찾을 수 없습니다.");

        } catch (Exception e) {
            log.error("[Gemini] 이미지 응답 파싱 실패", e);
            throw new RuntimeException("Gemini 이미지 응답 파싱 실패", e);
        }
    }
}
