package com.flowerable.spring.application.shop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopUpdateInfoReq {
    @NotNull
    private String shopName;

    private String description;

    @Pattern(
            regexp = "^[0-9]{9,11}$",
            message = "전화번호는 숫자 9~11자리여야 합니다."
    )
    private String telnum;

    @NotNull
    private String regionCode;

    @NotNull
    private String districtCode;

    @NotNull
    private String address;
    private Double latitude;
    private Double longitude;
}
