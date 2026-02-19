package com.flowerable.spring.service.gemini;

import com.flowerable.spring.dto.buquet.BouquetPreviewReq;
import com.flowerable.spring.entity.order.OrderItem;
import com.flowerable.spring.infra.gemini.GeminiClient;
import com.flowerable.spring.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BouquetPreviewService {
    private final BouquetPromptBuilder bouquetPromptBuilder;
    private final GeminiClient geminiClient;
    private final S3Service s3Service;

    /**
     * 결제 전 미리보기 — DTO 기반으로 Gemini 이미지 생성 후 S3 업로드, URL 반환
     */
    public String generatePreviewFromReq(BouquetPreviewReq req) {
        log.info("[BouquetPreview] 미리보기 생성 시작 - items: {}, wrapping: {}",
                req.getOrderItems().size(), req.getWrappingColorName());

        String prompt = bouquetPromptBuilder.buildFromPreviewReq(req);
        log.debug("[BouquetPreview] 생성된 프롬프트: {}", prompt);

        String base64 = geminiClient.generateImage(prompt);
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        String imageUrl = s3Service.uploadPreview(imageBytes);

        log.info("[BouquetPreview] 미리보기 생성 완료 - url: {}", imageUrl);
        return imageUrl;
    }
}
