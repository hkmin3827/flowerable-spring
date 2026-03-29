package com.flowerable.spring.domain.cart.entity;

import com.flowerable.spring.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public static Cart create(User user) {
        Cart cart = new Cart();
        cart.user = user;
        return cart;
    }
    
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.assignCart(this);
    }
    
    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.assignCart(null);
    }
    
    public int getTotalItemCount() {
        return cartItems.size();
    }
}
