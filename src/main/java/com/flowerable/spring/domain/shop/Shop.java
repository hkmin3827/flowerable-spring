package com.flowerable.spring.domain.shop;

import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import com.flowerable.spring.application.shop.dto.ShopUpdateInfoReq;
import com.flowerable.spring.domain.auth.Account;
import com.flowerable.spring.domain.shopflower.ShopFlower;
import com.flowerable.spring.global.exception.CustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "shops",
        indexes = {
                @Index(name = "idx_shops_district_status", columnList = "district, status"),
                @Index(name = "idx_shops_region_status",   columnList = "region, status")
        }
)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false, length = 50)
    private String shopName;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 100)
    private String address;  // 상세주소

    // 카카오 맵 지도 웹 UI 추가 시 확장용
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private District district;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus status = ShopStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registerAt;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<ShopFlower> shopFlowers = new ArrayList<>();

    @PrePersist
    void onRegister() {
        this.registerAt = LocalDateTime.now();
    }

    public Shop(Account account, String shopName, String address, Region region, District district) {
        this.account = account;
        this.shopName = shopName;
        this.address = address;
        this.status = ShopStatus.PENDING;
        this.region = region;
        this.district = district;
    }


    public static Shop create(Account account, String shopName, String address, Region region, District district) {
        return new Shop(account, shopName, address, region, district);
    }

    public void activate() {
        this.status = ShopStatus.ACTIVE;
    }

    public void suspend() {
        this.status = ShopStatus.SUSPENDED;
    }

    public void reject() {this.status = ShopStatus.REJECTED; }

    public void updateInfo(ShopUpdateInfoReq req, Region region, District district) {
        if (req.getShopName() != null) {this.shopName = req.getShopName();}
        if (req.getDescription() != null) {this.description = req.getDescription();}
        if (req.getTelnum() != null) {this.account.setTelnum(req.getTelnum());}
        if (region != null) {this.region = region;}
        if (district != null) {this.district = district;}
        if (req.getAddress() != null) {this.address = req.getAddress();}

        if (req.getLatitude() != null && req.getLongitude() != null) {
            validateCoordinate(req.getLatitude(), req.getLongitude());
            this.latitude = req.getLatitude();
            this.longitude = req.getLongitude();
        }
    }

    private void validateCoordinate(Double lat, Double lng) {
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw new CustomException(ErrorCode.INVALID_COORDINATE);
        }
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = ShopStatus.SUSPENDED;
    }
}
