package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.region.District;
import com.flowerable.spring.constant.shop.ShopStatus;
import org.springframework.beans.factory.annotation.Value;

public interface AdminShopListRes {
    Long getId();
    String getAccount_Email();
    String getAccount_Telnum();
    String getShopName();

    // enum 내부 필드 접근
    @Value("#{target.region.description}")
    String getRegionDescription();

    @Value("#{target.district.description}")
    String getDistrictDescription();

    District getDistrict();
    String getAddress();
    ShopStatus getStatus();
}
