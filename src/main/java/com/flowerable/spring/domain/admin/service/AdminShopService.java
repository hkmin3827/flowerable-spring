package com.flowerable.spring.domain.admin.service;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import com.flowerable.spring.domain.admin.dto.AdminShopListRes;
import com.flowerable.spring.domain.shop.dto.ShopDetailRes;
import com.flowerable.spring.domain.shop.entity.Shop;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.ShopNotFoundException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import com.flowerable.spring.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminShopService {
    private final ShopRepository shopRepository;


    @Transactional(readOnly = true)
    public Page<AdminShopListRes> getShops(
            ShopStatus shopStatus,
            AccountStatus accountStatus,
            Pageable pageable
    ) {

        log.info("shopStatus = " + shopStatus );
        return shopRepository.findAdminShops(
                shopStatus,
                accountStatus,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<AdminShopListRes> searchShops(String keyword, Pageable pageable) {

        String normalizedKeyword =
                (keyword == null || keyword.isBlank())
                        ? null
                        : keyword.trim();

        return shopRepository.searchAdminShops(normalizedKeyword, pageable);
    }

    @Transactional
    public void changeShopStatus(Long shopId, ShopStatus targetStatus) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(ShopNotFoundException::new);

        if(shop.getAccount().getStatus() == AccountStatus.DELETED) {
            throw new CustomException(ErrorCode.DELETED_ACCOUNT);
        }

        if (shop.getStatus() == targetStatus) {
            throw new CustomException(ErrorCode.INVALID_SHOP_STATUS);
        }

        switch (targetStatus) {
            case ACTIVE -> shop.activate();
            case SUSPENDED -> shop.suspend();
            case REJECTED -> shop.reject();
            default -> throw new CustomException(ErrorCode.INVALID_SHOP_STATUS);
        }
    }

    @Transactional
    public void changeStatus(Long shopId, AccountStatus targetStatus) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(UserNotFoundException::new);

        switch (targetStatus){
            case ACTIVE -> shop.getAccount().activate();
            case SUSPENDED -> shop.getAccount().suspend();
            default -> throw new CustomException(ErrorCode.INVALID_ACCOUNT_STATUS);
        }
    }

    @Transactional(readOnly = true)
    public ShopDetailRes getShopDetails(Long shopId){
        Shop shop = shopRepository.findDetailById(shopId)
                .orElseThrow(ShopNotFoundException::new);

        return ShopDetailRes.builder()
                .id(shop.getId())
                .email(shop.getAccount().getEmail())
                .address(shop.getAddress())
                .registerAt(shop.getRegisterAt())
                .regionDesc(shop.getRegion().getDescription())
                .districtDesc(shop.getDistrict().getDescription())
                .shopName(shop.getShopName())
                .status(shop.getStatus())
                .deletedAt(shop.getDeletedAt())
                .telnum(shop.getAccount().getTelnum())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .description(shop.getDescription())
                .build();
    }

}
