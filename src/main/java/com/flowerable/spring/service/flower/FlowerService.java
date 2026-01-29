package com.flowerable.spring.service.flower;

import com.flowerable.spring.constant.ErrorCode;
import com.flowerable.spring.dto.flower.FlowerCreateReq;
import com.flowerable.spring.dto.flower.FlowerRes;
import com.flowerable.spring.entity.flower.Flower;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerService {
    private final FlowerRepository flowerRepository;

    public Long registerFlower(FlowerCreateReq dto){
        if (flowerRepository.existsByName(dto.getName())){
            throw new CustomException(ErrorCode.FLOWER_NAME_DUPLICATED);
        }
        Flower flower = new Flower(dto);

        return flowerRepository.save(flower).getId();
    }

    // 활성된 꽃 목로 조회
    @Transactional(readOnly = true)
    public List<FlowerRes> getActiveFlowers() {
        return flowerRepository.findAllByActiveTrue()
                .stream()
                .map(FlowerRes::new)
                .toList();
    }

    public void deactivateFlower(Long flowerId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLOWER_NAME_DUPLICATED));

        flower.deactivate();
    }
}
