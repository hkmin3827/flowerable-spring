package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.constant.region.District;
import com.flowerable.spring.constant.region.Region;
import com.flowerable.spring.constant.shop.ShopStatus;
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
