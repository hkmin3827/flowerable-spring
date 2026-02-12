package com.flowerable.spring.entity.cart;

import com.flowerable.spring.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;
    
    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemDetail> details = new ArrayList<>();
    
    @Column(length = 100)
    private String message;
    
    private String wrappingColorName;
    
    private Integer wrappingExtraPrice;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public static CartItem create(Shop shop, String wrappingColorName, Integer wrappingExtraPrice, String message) {
        CartItem cartItem = new CartItem();
        cartItem.shop = shop;
        cartItem.wrappingColorName = wrappingColorName;
        cartItem.wrappingExtraPrice = wrappingExtraPrice;
        cartItem.message = message;
        return cartItem;
    }
    
    public void assignCart(Cart cart) {
        this.cart = cart;
    }
    
    public void addDetail(CartItemDetail detail) {
        details.add(detail);
        detail.assignCartItem(this);
    }
    
    public void removeDetail(CartItemDetail detail) {
        details.remove(detail);
        detail.assignCartItem(null);
    }
    
    public void updateWrapping(String colorName, Integer extraPrice) {
        this.wrappingColorName = colorName;
        this.wrappingExtraPrice = extraPrice;
    }
    
    public void updateMessage(String message) {
        this.message = message;
    }
    
    public int calculateTotalPrice() {
        int flowerPrice = details.stream()
                .mapToInt(CartItemDetail::calculatePrice)
                .sum();
        int wrappingPrice = wrappingExtraPrice != null ? wrappingExtraPrice : 0;
        return flowerPrice + wrappingPrice;
    }
}
