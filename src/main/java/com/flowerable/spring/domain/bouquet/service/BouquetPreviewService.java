package com.flowerable.spring.domain.bouquet.service;

import com.flowerable.spring.domain.bouquet.dto.BouquetPreviewReq;
import com.flowerable.spring.infra.gemini.GeminiClient;
import com.flowerable.spring.infra.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class BouquetPreviewService {
    private final BouquetPromptBuilder bouquetPromptBuilder;
    private final GeminiClient geminiClient;
    private final S3Service s3Service;

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
