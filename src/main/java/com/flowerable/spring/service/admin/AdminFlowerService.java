package com.flowerable.spring.service.admin;

import com.flowerable.spring.constant.ErrorCode;
import com.flowerable.spring.dto.admin.AdminFlowerListRes;
import com.flowerable.spring.dto.flower.FlowerCreateReq;
import com.flowerable.spring.dto.flower.FlowerRes;
import com.flowerable.spring.entity.flower.Flower;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.repository.FlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminFlowerService {
    private final FlowerRepository flowerRepository;
    // 모든 꽃 조회 (관리자용)
    @Transactional(readOnly = true)
    public Page<AdminFlowerListRes> getAllFlowers(Boolean active, Pageable pageable){
        return flowerRepository.findByActiveCondition(active, pageable);
    }

    public Long registerFlower(FlowerCreateReq dto){
        if (flowerRepository.existsByName(dto.getName())){
            throw new CustomException(ErrorCode.FLOWER_NAME_DUPLICATED);
        }
        Flower flower = new Flower(dto);

        return flowerRepository.save(flower).getId();
    }

    public void deactivateFlower(Long flowerId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLOWER_NAME_DUPLICATED));

        flower.deactivate();
    }


}
