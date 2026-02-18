package com.flowerable.spring.entity.order;

import com.flowerable.spring.constant.shopflower.Color;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_request_id", nullable = false)
    private OrderRequest orderRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_flower_id", nullable = false)
    private ShopFlower shopFlower;

    @Column(nullable = false)
    private Integer quantity;   // 개수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Color flowerColor;

    @Column(nullable = false)
    private Integer basePrice;   // 꽃 1개당 가격 shopFlower.price

    public int calculateItemPrice() {
        return basePrice * quantity;
    }

    public void assignOrder(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public static OrderItem create(ShopFlower shopFlower, int quantity, Color color) {
        return OrderItem.builder()
                .shopFlower(shopFlower)
                .quantity(quantity)
                .flowerColor(color)
                .basePrice(shopFlower.getPrice())
                .build();
    }
}
