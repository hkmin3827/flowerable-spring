package com.flowerable.spring.service.admin;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.dto.admin.AdminFlowerListRes;
import com.flowerable.spring.dto.flower.FlowerCreateReq;
import com.flowerable.spring.dto.flower.FlowerUpdateInfoReq;
import com.flowerable.spring.entity.flower.Flower;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.repository.FlowerRepository;
import com.flowerable.spring.repository.ShopFlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFlowerService {
    private final FlowerRepository flowerRepository;
    private final ShopFlowerRepository shopFlowerRepository;
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

    @Transactional
    public void updateFlowerInfo(Long flowerId, FlowerUpdateInfoReq req){
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLOWER_NOT_FOUND));

        flower.updateInfo(req);
    }

    @Transactional
    public void deactivateFlower(Long flowerId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLOWER_NOT_FOUND));

        flower.deactivate();

        shopFlowerRepository.stopSaleByFlowerId(flower.getId());
    }

    @Transactional
    public void activateFlower(Long flowerId) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLOWER_NOT_FOUND));

        flower.activate();
    }
}
