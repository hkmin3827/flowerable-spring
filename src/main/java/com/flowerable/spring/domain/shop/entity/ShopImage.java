package com.flowerable.spring.entity.shop;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.exception.CustomException;
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
public class ShopImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false)
    private String imageUrl;


    @Column(nullable = false)
    private Boolean isThumbnail = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private ShopImage(Shop shop, String imageUrl, boolean isThumbnail) {
        this.shop = shop;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
    }

    public static ShopImage create(Shop shop, String imageUrl) {
        ShopImage image = new ShopImage();
        image.shop = shop;
        image.imageUrl = imageUrl;
        image.isThumbnail = false;

        return image;
    }

    public void registerThumbnail(){
        if(this.isThumbnail == true){
            throw new CustomException(ErrorCode.IMAGE_ALREADY_THUMBNAIL);
        }
        this.isThumbnail = true;
    }


    public void clearThumbnail(){
        if(this.isThumbnail == false){
            throw new CustomException(ErrorCode.IMAGE_ALREADY_NOT_THUMBNAIL);
        }
        this.isThumbnail = false;
    }
}
