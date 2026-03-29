package com.flowerable.spring.application.admin;

import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.application.admin.dto.AdminFlowerListRes;
import com.flowerable.spring.application.flower.dto.FlowerCreateReq;
import com.flowerable.spring.application.flower.dto.FlowerUpdateInfoReq;
import com.flowerable.spring.domain.flower.Flower;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.domain.flower.FlowerRepository;
import com.flowerable.spring.domain.shopflower.ShopFlowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminFlowerService {
    private final FlowerRepository flowerRepository;
    private final ShopFlowerRepository shopFlowerRepository;

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
