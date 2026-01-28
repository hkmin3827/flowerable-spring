package com.flowerable.spring.service.admin;

import com.flowerable.spring.dto.shop.ShopDetailRes;
import com.flowerable.spring.dto.user.UserDetailRes;
import com.flowerable.spring.entity.User;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.repository.ShopRepository;
import com.flowerable.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public UserDetailRes getUserDetails(Long accountId) {
        User user = userRepository
                .findByAccountId(accountId)
                .orElseThrow(UserNotFoundException::new);

        return UserDetailRes.builder()
                .id(user.getId())
                .email(user.getAccount().getEmail())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .name(user.getName())
                .deletedAt(user.getDeletedAt())
                .telnum(user.getTelnum())
                .active(user.isActive())
                .provider(user.getAccount().getProvider())
                .providerId(user.getAccount().getProviderId())
                .build();
    }

    public ShopDetailRes getShopDetails(Long accountId){
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
