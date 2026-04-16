package com.flowerable.spring.application.shopflower;

import com.flowerable.spring.application.common.ShopCacheService;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.application.shopflower.dto.*;
import com.flowerable.spring.domain.flower.Flower;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.domain.shopflower.ShopFlower;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.ShopNotFoundException;
import com.flowerable.spring.domain.flower.FlowerRepository;
import com.flowerable.spring.domain.shopflower.ShopFlowerRepository;
import com.flowerable.spring.domain.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopFlowerService {
    private final ShopFlowerRepository shopFlowerRepository;
    private final ShopRepository shopRepository;
    private final FlowerRepository flowerRepository;
    private final ShopCacheService shopCacheService;

    @Transactional
    public void register(Long accountId, ShopFlowerRegReq req) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new AccessDeniedException("SHOP 권한이 필요합니다."));

        if (shopFlowerRepository.existsByShopIdAndFlowerId(shop.getId(), req.getFlowerId())) {
            throw new CustomException(ErrorCode.SHOP_FLOWER_ALREADY_REGISTER);
        }

        Flower flower = flowerRepository.findByIdAndActiveTrue(req.getFlowerId())
                .orElseThrow(() -> new CustomException(ErrorCode.FLOWER_NOT_ACTIVE));

        ShopFlower shopFlower = new ShopFlower(
                shop,
                flower,
                req.getPrice(),
                new ArrayList<>(req.getColors())
        );

        shopFlowerRepository.save(shopFlower);
        shopCacheService.evictByRegion(shop.getRegion());
    }

    @Transactional
    public void updateOption(Long accountId, Long shopFlowerId, ShopFlowerUpdateReq req){
        ShopFlower shopFlower = shopFlowerRepository.findWithShopAndAccount(shopFlowerId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_FLOWER_NOT_REGISTER));

        if(!shopFlower.getShop().getAccount().getId().equals(accountId)) {
            throw new AccessDeniedException("해당 Shop 계정과 일치하지 않습니다.");
        }

        if (req.getColors() != null && req.getColors().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FLOWER_COLORS);
        }

        shopFlower.updateInfo(req.getPrice(), req.getColors());
    }

    @Transactional
    public void activate(Long accountId, Long shopFlowerId) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        ShopFlower shopFlower = shopFlowerRepository
                .findByIdAndShopId(shopFlowerId, shop.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_FLOWER_NOT_REGISTER));

        if (!shopFlower.getFlower().getActive()) {
            throw new CustomException(ErrorCode.FLOWER_NOT_ACTIVE);
        }

        shopFlower.startSale();
        shopCacheService.evictByRegion(shop.getRegion());
    }

    @Transactional
    public void deactivate(Long accountId, Long shopFlowerId) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));

        ShopFlower shopFlower = shopFlowerRepository
                .findByIdAndShopId(shopFlowerId, shop.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_FLOWER_NOT_REGISTER));

        shopFlower.stopSale();
        shopCacheService.evictByRegion(shop.getRegion());
    }

    @Transactional(readOnly = true)
    public Page<ShopFlowerRes> getMyShopFlowers(
            Long accountId,
            Boolean isOnSale,
            Pageable pageable
    ) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        return shopFlowerRepository.findMyShopFlowers(
                shop.getId(),
                isOnSale,
                pageable
        ).map(sf -> ShopFlowerRes.builder()
                .id(sf.getId())
                .flowerId(sf.getFlower().getId())
                .flowerName(sf.getFlower().getName())
                .price(sf.getPrice())
                .onSale(sf.getOnSale())
                .colors(new ArrayList<>(sf.getColors()))
                .build()
        );
    }

    @Transactional(readOnly = true)
    public List<ShopFlowerOrderStatsRes> getTop5FlowerStats(Long accountId) {

        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        List<ShopFlowerOrderCountDto> results =
                shopFlowerRepository.findTop5FlowersByOrderCount(
                        shop.getId(),
                        PageRequest.of(0, 5)
                );

        int rank = 1;
        List<ShopFlowerOrderStatsRes> stats = new ArrayList<>();

        for (ShopFlowerOrderCountDto dto : results) {
            stats.add(new ShopFlowerOrderStatsRes(
                    rank++,
                    dto.flowerName(),
                    dto.orderCount()
            ));
        }

        return stats;
    }
}
