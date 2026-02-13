package com.flowerable.spring.service.admin;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.shop.ShopStatus;
import com.flowerable.spring.dto.admin.AdminShopListRes;
import com.flowerable.spring.dto.shop.ShopDetailRes;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminShopService {
    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public Page<AdminShopListRes> getShopsByStatus(ShopStatus status, Pageable pageable) {
        if (status == null) {
            return shopRepository.findAllAdminShops(pageable);
        }
        return shopRepository.findAdminShopsByStatus(status, pageable);
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
    public void changeStatus(Long shopId, ShopStatus targetStatus) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(ShopNotFoundException::new);

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


    @Transactional(readOnly = true)
    public ShopDetailRes getShopDetails(Long shopId){
        Shop shop = shopRepository.findDetailById(shopId)
                .orElseThrow(ShopNotFoundException::new);

        return ShopDetailRes.builder()
                .id(shop.getId())
                .email(shop.getAccount().getEmail())
                .address(shop.getAddress())
                .registerAt(shop.getRegisterAt())
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
