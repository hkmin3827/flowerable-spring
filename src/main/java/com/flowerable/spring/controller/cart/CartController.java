package com.flowerable.spring.controller.cart;

import com.flowerable.spring.dto.cart.CartRequest;
import com.flowerable.spring.dto.cart.CartResponse;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    /**
     * 장바구니 조회
     */
    @GetMapping
    public ResponseEntity<CartResponse.CartInfo> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CartResponse.CartInfo cart = cartService.getCart(userDetails.getId());
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 장바구니 아이템 개수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<CartResponse.CartCount> getCartCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CartResponse.CartCount count = cartService.getCartCount(userDetails.getId());
        return ResponseEntity.ok(count);
    }
    
    /**
     * 장바구니에 추가
     */
    @PostMapping
    public ResponseEntity<CartResponse.CartInfo> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CartRequest.AddToCart request
    ) {
        CartResponse.CartInfo cart = cartService.addToCart(userDetails.getId(), request);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 장바구니 항목 삭제
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<CartResponse.CartInfo> removeCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId
    ) {
        CartResponse.CartInfo cart = cartService.removeCartItem(userDetails.getId(), cartItemId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 장바구니 항목 수정
     */
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartResponse.CartInfo> updateCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestBody CartRequest.UpdateCartItem request
    ) {
        CartResponse.CartInfo cart = cartService.updateCartItem(userDetails.getId(), cartItemId, request);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 장바구니 전체 비우기
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
