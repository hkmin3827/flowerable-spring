package com.flowerable.spring.entity.shop;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean isThumbnail;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private ShopImages(Shop shop, String imageUrl, int sortOrder, boolean isThumbnail) {
        this.shop = shop;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.isThumbnail = isThumbnail;
    }

    public static ShopImages create(Shop shop, String imageUrl, int order, boolean thumbnail) {
        return new ShopImages(shop, imageUrl, order, thumbnail);
    }
}
