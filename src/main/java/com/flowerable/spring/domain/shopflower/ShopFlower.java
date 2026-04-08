package com.flowerable.spring.domain.shopflower;

import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.flower.Flower;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.global.exception.CustomException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "shop_flowers",
        indexes = {
                @Index(name = "idx_shop_flowers_shop_on_sale", columnList = "shop_id, on_sale")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShopFlower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id", nullable = false)
    private Flower flower;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Boolean onSale = true;

    @BatchSize(size = 20)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "shop_flower_colors",
            joinColumns = @JoinColumn(name = "shop_flower_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "color", length = 20)
    private Set<Color> colors = new HashSet<>();

    public ShopFlower(Shop shop, Flower flower, Integer price, List<Color> colors){
        this.shop = shop;
        this.flower = flower;
        this.price = price;
        this.onSale = true;
        this.colors = new HashSet<>(colors);

        shop.getShopFlowers().add(this);
    }

    public void updateInfo(Integer price, List<Color> colors) {
        if (price != null) {
            this.price = price;
        }

        if (colors != null) {
            this.colors = new HashSet<>(colors);
        }
    }

    public void startSale() {
        if(Boolean.TRUE.equals(this.onSale)){
            throw new CustomException(ErrorCode.SHOP_FLOWER_ALREADY_ONSALE);
        }
        this.onSale = true;
    }

    public void stopSale(){
        if(Boolean.FALSE.equals(this.onSale)){
            throw new CustomException(ErrorCode.SHOP_FLOWER_ALREADY_STOPSALE);
        }
        this.onSale = false;
    }

}
