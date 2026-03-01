package com.flowerable.spring.domain.cart.entity;

import com.flowerable.spring.domain.shopflower.constant.Color;
import com.flowerable.spring.domain.shop.entity.Shop;
import com.flowerable.spring.domain.shopflower.entity.ShopFlower;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public static CartItem create(Shop shop) {
        CartItem cartItem = new CartItem();
        cartItem.shop = shop;
        return cartItem;
    }
    
    public void assignCart(Cart cart) {
        this.cart = cart;
    }

    private void addDetail(CartItemDetail detail) {
        this.details.add(detail);
        detail.assignCartItem(this);
    }

    public void addFlower(ShopFlower shopFlower, int quantity, Color color) {
        Optional<CartItemDetail> existing = this.details.stream()
                .filter(d ->
                        d.getShopFlower().getId().equals(shopFlower.getId())
                                && d.getFlowerColor() == color
                )
                .findFirst();

        if (existing.isPresent()) {
            existing.get().increaseQuantity(quantity);
        } else {
            CartItemDetail detail = CartItemDetail.create(
                    shopFlower,
                    quantity,
                    color
            );
            addDetail(detail);
        }
    }
}
