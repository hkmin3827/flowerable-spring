package com.flowerable.spring.application.shop;

import com.flowerable.spring.application.common.ShopCacheService;
import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.application.shop.dto.ShopDetailRes;
import com.flowerable.spring.application.shop.dto.ShopSearchRes;
import com.flowerable.spring.application.shop.dto.ShopUpdateInfoReq;
import com.flowerable.spring.application.shopflower.dto.ShopFlowerRes;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.domain.shopflower.ShopFlower;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.ShopNotFoundException;
import com.flowerable.spring.domain.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final ShopCacheService shopCacheService;

    @Transactional(readOnly = true)
    public Page<ShopSearchRes> searchShops(
            String flowerName,
            String regionDesc,
            String districtDesc,
            Pageable pageable
    ) {
        Region region = Region.fromSearchParams(regionDesc);
        District district = District.fromSearchParams(districtDesc);

        validateRegionDistrict(region, district);
        return shopRepository.findShopsByFilter(
                flowerName,
                region,
                district,
                pageable
        );
    }

    @Transactional
    public void updateShopInfo(Long accountId, ShopUpdateInfoReq req) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        Region oldRegion = shop.getRegion();
        District oldDistrict = shop.getDistrict();

        Region region = Region.fromCode(req.getRegionCode());
        District district = District.fromCode(req.getDistrictCode());

        shop.updateInfo(req, region, district);

        boolean regionChanged = !oldRegion.equals(region);
        boolean districtChanged = !oldDistrict.equals(district);

        if (regionChanged) {
            shopCacheService.evictByRegion(oldRegion);
            shopCacheService.evictByRegion(region);
        } else if (districtChanged) {
            shopCacheService.evictByRegion(region);
        }
    }

    @Transactional(readOnly = true)
    public ShopDetailRes getDetails(Long shopId){
        Shop shop = shopRepository. findDetailWithFlowers(shopId)
                .orElseThrow(ShopNotFoundException::new);

        List<ShopFlowerRes> flowerResList =
                shop.getShopFlowers().stream()
                        .filter(ShopFlower::getOnSale)     // 판매 중인 것만 조회 필터링
                        .map(sf -> ShopFlowerRes.builder()
                                .id(sf.getId())
                                .flowerId(sf.getFlower().getId())
                                .flowerName(sf.getFlower().getName())
                                .price(sf.getPrice())
                                .onSale(sf.getOnSale())
                                .colors(new ArrayList<>(sf.getColors()))
                                .build()
                        )
                        .toList();

        return ShopDetailRes.builder()
                .id(shop.getId())
                .email(shop.getAccount().getEmail())
                .address(shop.getAddress())
                .region(shop.getRegion())
                .district(shop.getDistrict())
                .regionDesc(shop.getRegion().getDescription())
                .districtDesc(shop.getDistrict().getDescription())
                .registerAt(shop.getRegisterAt())
                .shopName(shop.getShopName())
                .status(shop.getStatus())
                .deletedAt(shop.getDeletedAt())
                .telnum(shop.getAccount().getTelnum())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .description(shop.getDescription())
                .shopFlowers(flowerResList)
                .accountStatus(shop.getAccount().getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public ShopDetailRes getMyDetails(Long accountId){
        Shop shop = shopRepository.findMyDetail(accountId)
                .orElseThrow(ShopNotFoundException::new);

        // 디테일 페이지에서는 shopFlower onSale = true인것만 필터링, 수정 들어가면 전체 조회
        List<ShopFlowerRes> flowerResList =
                shop.getShopFlowers().stream()
                        .filter(ShopFlower::getOnSale)
                        .map(sf -> ShopFlowerRes.builder()
                                .id(sf.getId())
                                .flowerId(sf.getFlower().getId())
                                .flowerName(sf.getFlower().getName())
                                .price(sf.getPrice())
                                .onSale(sf.getOnSale())
                                .colors(new ArrayList<>(sf.getColors()))
                                .build()
                        )
                        .toList();

        return ShopDetailRes.builder()
                .id(shop.getId())
                .email(shop.getAccount().getEmail())
                .address(shop.getAddress())
                .region(shop.getRegion())
                .district(shop.getDistrict())
                .regionDesc(shop.getRegion().getDescription())
                .districtDesc(shop.getDistrict().getDescription())
                .registerAt(shop.getRegisterAt())
                .shopName(shop.getShopName())
                .status(shop.getStatus())
                .deletedAt(shop.getDeletedAt())
                .telnum(shop.getAccount().getTelnum())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .description(shop.getDescription())
                .shopFlowers(flowerResList)
                .accountStatus(shop.getAccount().getStatus())
                .build();
    }

    private void validateRegionDistrict(Region region, District district){
        if(district == null || region == null){
            return;
        }

        if(!District.findByRegion(region).contains(district)){
            throw new CustomException(ErrorCode.INVALID_LOCATION);
        }
    }
}
