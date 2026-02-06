package com.flowerable.spring.dto.shop;

import com.flowerable.spring.constant.region.District;
import com.flowerable.spring.constant.region.Region;
import com.flowerable.spring.constant.shop.ShopStatus;
import com.flowerable.spring.dto.shopflower.ShopFlowerRes;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// shop 정보 조회(user) / shop profile
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

    private List<ShopFlowerRes> shopFlowers;
}
