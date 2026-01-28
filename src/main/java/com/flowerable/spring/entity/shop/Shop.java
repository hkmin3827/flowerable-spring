package com.flowerable.spring.entity.shop;

import com.flowerable.spring.constant.ShopStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LOCAL 로그인만 허용
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String shopName;

    @Column(nullable = true, length = 255)
    private String description;

    @Column(length=20)
    private String telNum;

    @Column(nullable = false, length = 100)
    private String address;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus status = ShopStatus.PENDING;

    // shop 계정 가입(등록) 시점
    @Column(nullable = false, updatable = false)
    private LocalDateTime registerAt;

    private LocalDateTime deletedAt;

    @PrePersist
    public void onRegister() {this.registerAt = LocalDateTime.now();}

    public void activate() {this.status = ShopStatus.ACTIVE;}
    public void suspend(){this.status = ShopStatus.SUSPENDED;}







}
