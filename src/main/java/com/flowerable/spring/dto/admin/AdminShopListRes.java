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

    // JPQL에서 'region'으로 가져온 Enum 객체의 getDescription()을 호출
    @Value("#{target.region.description}")
    String getRegionDescription();

    // JPQL에서 'district'로 가져온 Enum 객체의 getDescription()을 호출
    @Value("#{target.district.description}")
    String getDistrictDescription();

    Region getRegion();
    District getDistrict();
    String getAddress();
    ShopStatus getStatus();
    LocalDateTime getRegisterAt();

}
