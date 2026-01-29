package com.flowerable.spring.service.shopflower;

import com.flowerable.spring.dto.shopflower.ShopFlowerRegReq;
import com.flowerable.spring.entity.flower.Flower;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import com.flowerable.spring.repository.FlowerRepository;
import com.flowerable.spring.repository.ShopFlowerRepository;
import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ShopFlowerService {
    private final ShopFlowerRepository shopFlowerRepository;
    private final ShopRepository shopRepository;
    private final FlowerRepository flowerRepository;

    @Transactional
    public void register(Long shopId, ShopFlowerRegReq req) {
        if (shopFlowerRepository.existsByShopIdAndFlowerId(shopId, req.getFlowerId())) {
            throw new IllegalStateException("이미 등록된 꽃입니다.");
        }

        Shop shop = shopRepository.getReferenceById(shopId);
        Flower flower = flowerRepository.getReferenceById(req.getFlowerId());

        ShopFlower shopFlower = new ShopFlower(
                shop,
                flower,
                req.getPrice(),
                new ArrayList<>(req.getColors())
        );

        shopFlowerRepository.save(shopFlower);
    }
}
