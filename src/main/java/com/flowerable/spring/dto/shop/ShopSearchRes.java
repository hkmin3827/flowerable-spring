package com.flowerable.spring.dto.shop;

import org.springframework.beans.factory.annotation.Value;

public interface ShopSearchRes {
    Long getId();
    String getShopName();
    String getDescription();
    String getAddress();
    String getTelnum();

    @Value("#{target.region.description}")
    String getRegionDescription();

    @Value("#{target.district.description}")
    String getDistrictDescription();
}