package com.flowerable.spring.service.flower;

import com.flowerable.spring.constant.flower.Season;
import com.flowerable.spring.dto.flower.FlowerRes;
import com.flowerable.spring.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlowerService {
    private final FlowerRepository flowerRepository;
    // 활성된 꽃 목록 조회
    @Transactional(readOnly = true)
    public Page<FlowerRes> getActiveFlowers(Season category, Pageable pageable) {

        return flowerRepository.findUserFlowersByCategory(category, pageable)
                .map(FlowerRes::new);
    }

}
