package com.flowerable.spring.application.flower;

import com.flowerable.spring.domain.flower.Season;
import com.flowerable.spring.application.flower.dto.FlowerRes;
import com.flowerable.spring.domain.flower.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlowerService {
    private final FlowerRepository flowerRepository;

    @Transactional(readOnly = true)
    public Page<FlowerRes> getActiveFlowers(Season category, Pageable pageable) {

        return flowerRepository.findUserFlowersByCategory(category, pageable)
                .map(FlowerRes::new);
    }

}
