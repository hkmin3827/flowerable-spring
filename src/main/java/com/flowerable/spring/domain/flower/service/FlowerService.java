package com.flowerable.spring.domain.flower.service;

import com.flowerable.spring.domain.flower.constant.Season;
import com.flowerable.spring.domain.flower.dto.FlowerRes;
import com.flowerable.spring.domain.flower.repository.FlowerRepository;
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
