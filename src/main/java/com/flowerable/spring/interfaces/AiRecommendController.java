package com.flowerable.spring.interfaces;

import com.flowerable.spring.application.ai.AiRecommendService;
import com.flowerable.spring.application.ai.dto.ContentReq;
import com.flowerable.spring.application.ai.dto.ChatBotRecommendRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiRecommendController {

    private final AiRecommendService aiRecommendService;

    @PostMapping("/recommend")
    public Mono<ResponseEntity<ChatBotRecommendRes>> recommend(
            @Valid @RequestBody ContentReq req
    ) {
        return aiRecommendService.recommend(req)
                .map(ResponseEntity::ok);
    }
}
