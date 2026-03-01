package com.flowerable.spring.domain.admin.dto;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface AdminShopListRes {
    Long getId();
    String getAccountEmail();
    String getAccountTelnum();
    AccountStatus getAccountStatus();
    String getShopName();

    @Value("#{target.region.description}")
    String getRegionDescription();

    @Value("#{target.district.description}")
    String getDistrictDescription();

    Region getRegion();
    District getDistrict();
    String getAddress();
    ShopStatus getStatus();
    LocalDateTime getRegisterAt();

}
