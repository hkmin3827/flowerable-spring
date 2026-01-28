package com.flowerable.spring.entity.shop;

import com.flowerable.spring.constant.ShopStatus;
import com.flowerable.spring.entity.account.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String address;

    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus status = ShopStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registerAt;

    private LocalDateTime deletedAt;

    @PrePersist
    void onRegister() {
        this.registerAt = LocalDateTime.now();
    }

    private Shop(Account account, String shopName, String address, String telnum) {
        this.account = account;
        this.shopName = shopName;
        this.address = address;
        this.telnum = telnum;
        this.status = ShopStatus.PENDING;
    }

    public static Shop create(Account account, String shopName, String address, String telnum) {
        return new Shop(account, shopName, address, telnum);
    }

    public void activate() {
        this.status = ShopStatus.ACTIVE;
    }

    public void suspend() {
        this.status = ShopStatus.SUSPENDED;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = ShopStatus.SUSPENDED;
    }
}
