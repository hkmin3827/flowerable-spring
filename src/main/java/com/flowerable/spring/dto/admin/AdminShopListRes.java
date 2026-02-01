package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.District;
import com.flowerable.spring.constant.ShopStatus;
import org.springframework.beans.factory.annotation.Value;

public interface AdminShopListRes {
    Long getId();
    String getAccountEmail();
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
