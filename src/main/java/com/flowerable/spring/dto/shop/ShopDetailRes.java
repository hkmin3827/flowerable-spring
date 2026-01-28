package com.flowerable.spring.dto.shop;

import com.flowerable.spring.constant.ShopStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
public class ShopDetailRes {
    private Long id;
    private String email;
    private String shopName;
    private String description;
    private String telnum;
    private String address;
    private Double latitude;
    private Double longitude;
    private ShopStatus status;
    private LocalDateTime deletedAt;
    private LocalDateTime registerAt;
}
