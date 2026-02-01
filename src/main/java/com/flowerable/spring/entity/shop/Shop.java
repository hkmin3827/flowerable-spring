package com.flowerable.spring.entity.shop;

import com.flowerable.spring.constant.District;
import com.flowerable.spring.constant.ErrorCode;
import com.flowerable.spring.constant.Region;
import com.flowerable.spring.constant.ShopStatus;
import com.flowerable.spring.dto.shop.ShopDetailRes;
import com.flowerable.spring.dto.shop.ShopUpdateInfoReq;
import com.flowerable.spring.entity.account.Account;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import com.flowerable.spring.exception.CustomException;
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
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Account와 1:1
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false, length = 50)
    private String shopName;

    @Column(length = 255)
    private String description;

    @Column(length = 20)
    private String telnum;

    @Column(nullable = false, length = 100)
    private String address;  // 상세주소

    // map 표시용
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

    private Shop(Account account, String shopName, String address, String telnum, Region region, District district) {
        this.account = account;
        this.shopName = shopName;
        this.address = address;
        this.telnum = telnum;
        this.status = ShopStatus.PENDING;
        this.region = region;
        this.district = district;
    }


    public static Shop create(Account account, String shopName, String address, String telnum, Region region, District district) {
        return new Shop(account, shopName, address, telnum, region, district);
    }

    public void activate() {
        this.status = ShopStatus.ACTIVE;
    }

    public void suspend() {
        this.status = ShopStatus.SUSPENDED;
    }

    public void updateInfo(ShopUpdateInfoReq req, Region region, District district) {
        if (req.getShopName() != null) {this.shopName = req.getShopName();}
        if (req.getDescription() != null) {this.description = req.getDescription();}
        if (req.getTelnum() != null) {this.telnum = req.getTelnum();}
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
