package com.flowerable.spring.dto.shop;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopUpdateInfoReq {
    private String shopName;
    private String description;
    private String telnum;
    private String regionDesc;
    private String districtDesc;
    private String address;
    private Double latitude;
    private Double longitude;
}
