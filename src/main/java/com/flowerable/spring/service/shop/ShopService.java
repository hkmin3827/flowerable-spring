package com.flowerable.spring.service.shop;

import com.flowerable.spring.dto.shop.ShopDetailRes;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public ShopDetailRes getMyDetails(Long accountId){
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        return ShopDetailRes.builder()
                .id(shop.getId())
                .email(shop.getAccount().getEmail())
                .address(shop.getAddress())
                .registerAt(shop.getRegisterAt())
                .shopName(shop.getShopName())
                .status(shop.getStatus())
                .deletedAt(shop.getDeletedAt())
                .telnum(shop.getTelnum())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .description(shop.getDescription())
                .build();
    }
}
