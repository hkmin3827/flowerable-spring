package com.flowerable.spring.domain.shop.dto;

import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import lombok.Getter;

@Getter
public class ShopSearchRes {

    private final Long shopId;
    private final String shopName;
    private final String telnum;
    private final String description;
    private final String address;
    private final String regionDesc;
    private final String districtDesc;

    public ShopSearchRes(Long shopId, String shopName, String telnum, String description, String address, Region region, District district) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.telnum = telnum;
        this.description = description;
        this.address = address;
        this.regionDesc = region.getDescription();
        this.districtDesc = district.getDescription();
    }
}