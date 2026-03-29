package com.flowerable.spring.application.shop.dto;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import com.flowerable.spring.application.shopflower.dto.ShopFlowerRes;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter @Setter
public class ShopDetailRes {
    private final Long id;
    private final String email;
    private final String shopName;
    private final String description;
    private final String telnum;
    private final String address;
    private final Double latitude;
    private final Double longitude;
    private final Region region;
    private final District district;
    private final String regionDesc;
    private final String districtDesc;
    private final ShopStatus status;
    private final LocalDateTime deletedAt;
    private final LocalDateTime registerAt;

    private final List<ShopFlowerRes> shopFlowers;
    private final AccountStatus accountStatus;
}
