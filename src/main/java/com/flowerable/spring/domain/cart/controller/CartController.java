package com.flowerable.spring.domain.cart.controller;

import com.flowerable.spring.domain.cart.dto.CartRequest;
import com.flowerable.spring.domain.cart.dto.CartResponse;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping
    public ResponseEntity<CartResponse.CartInfo> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CartResponse.CartInfo cart = cartService.getCart(userDetails.getId());
        return ResponseEntity.ok(cart);
    }
    
    @GetMapping("/count")
    public ResponseEntity<CartResponse.CartCount> getCartCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CartResponse.CartCount count = cartService.getCartCount(userDetails.getId());
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<CartResponse.CartInfo> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CartRequest.AddToCart request
    ) {
        CartResponse.CartInfo cart = cartService.addToCart(userDetails.getId(), request);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<CartResponse.CartInfo> removeCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId
    ) {
        CartResponse.CartInfo cart = cartService.removeCartItem(userDetails.getId(), cartItemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
