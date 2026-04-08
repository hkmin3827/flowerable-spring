package com.flowerable.spring.interfaces;

import com.flowerable.spring.application.ai.AiRecommendService;
import com.flowerable.spring.application.ai.dto.FlowerRecommendRequest;
import com.flowerable.spring.application.ai.dto.FlowerRecommendResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiRecommendController {

    private final AiRecommendService aiRecommendService;

    /**
     * POST /api/v1/ai/recommend
     *
     * 사용자의 상황(purpose)과 선택적 지역(location)을 AI 에이전트에 전달하여
     * 꽃 추천 및 꽃집 추천 결과를 반환한다.
     * 응답 데이터는 프론트엔드 챗봇 세션 내에서만 유효하며 서버에 저장되지 않는다.
     *
     * Request Body:
     *   - purpose  (필수): 사용자 상황 설명 (예: "친구에게 사과하고 싶어요")
     *   - location (선택): 지역명 (예: "서울"). 없으면 꽃 추천만 수행.
     *
     * Response:
     *   - recommendation : 추천 이유 + 부케 디자인 (마크다운)
     *   - flowers        : 추천 꽃 목록 (이름, 꽃말, 역할)
     *   - shops          : 추천 꽃집 목록 (location 미전달 시 빈 배열)
     *   - phase          : 처리 단계 ("FLOWER_ONLY" | "FLOWER_AND_SHOP")
     */
    @PostMapping("/recommend")
    public ResponseEntity<FlowerRecommendResponse> recommend(
            @Valid @RequestBody FlowerRecommendRequest req
    ) {
        FlowerRecommendResponse response = aiRecommendService.recommend(req);
        return ResponseEntity.ok(response);
    }
}
