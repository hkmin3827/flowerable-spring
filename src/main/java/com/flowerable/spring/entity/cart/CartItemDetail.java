package com.flowerable.spring.entity.cart;

import com.flowerable.spring.constant.shopflower.Color;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_item_details")
@Getter
@NoArgsConstructor
public class CartItemDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_id", nullable = false)
    private CartItem cartItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_flower_id", nullable = false)
    private ShopFlower shopFlower;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Color flowerColor;
    
    @Column(nullable = false)
    private Integer basePrice;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public static CartItemDetail create(ShopFlower shopFlower, int quantity, Color color) {
        CartItemDetail detail = new CartItemDetail();
        detail.shopFlower = shopFlower;
        detail.quantity = quantity;
        detail.flowerColor = color;
        detail.basePrice = shopFlower.getPrice();
        return detail;
    }
    
    public void assignCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
    
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int calculatePrice() {
        return basePrice * quantity;
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

}
