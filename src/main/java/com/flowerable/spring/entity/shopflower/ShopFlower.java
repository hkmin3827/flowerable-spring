package com.flowerable.spring.entity.shopflower;

import com.flowerable.spring.constant.Color;
import com.flowerable.spring.constant.Season;
import com.flowerable.spring.entity.flower.Flower;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.repository.ShopFlowerRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shop_flowers")
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
    private Boolean onSale = true; // 판매 여부

    public void NotOnSale(){
        this.onSale = false;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "shop_flower_colors",
            joinColumns = @JoinColumn(name = "shop_flower_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "color", length = 20)
    private List<Color> colors = new ArrayList<>();

    public ShopFlower(Shop shop, Flower flower, Integer price, List<Color> colors){
        this.shop = shop;
        this.flower = flower;
        this.price = price;
        this.onSale = true;
    }

    public void updateInfo(Integer price, Boolean onSale, List<Color> colors) {
        if (price != null) {
            this.price = price;
        }
        if (onSale != null) {
            this.onSale = onSale;
        }
        
        // 프론트에서 기존 색상 + 추가 색상 full로 내려줌
        if (colors != null) {
            this.colors.clear();
            this.colors.addAll(colors);
        }
    }
}
