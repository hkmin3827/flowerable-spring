package com.flowerable.spring.domain.cart.dto;

import com.flowerable.spring.domain.shopflower.constant.Color;
import com.flowerable.spring.domain.cart.entity.Cart;
import com.flowerable.spring.domain.cart.entity.CartItem;
import com.flowerable.spring.domain.cart.entity.CartItemDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CartResponse {
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartInfo {
        private Long cartId;
        private Integer totalShopCount;
        private List<CartItemInfo> items;
        private Integer totalPrice;
        
        public static CartInfo from(Cart cart) {
            List<CartItemInfo> items = cart.getCartItems().stream()
                    .map(CartItemInfo::from)
                    .collect(Collectors.toList());
            
            int totalPrice = items.stream()
                    .mapToInt(CartItemInfo::getTotalPrice)
                    .sum();
            
            return CartInfo.builder()
                    .cartId(cart.getId())
                    .totalShopCount(cart.getTotalItemCount())
                    .items(items)
                    .totalPrice(totalPrice)
                    .build();
        }
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemInfo {
        private Long cartItemId;
        private Long shopId;
        private String shopName;
        private List<FlowerDetailInfo> flowers;
        private Integer totalFlowerPrice;
        private Integer totalPrice;
        private LocalDateTime createdAt;
        private String shopAddress;

        public static CartItemInfo from(CartItem cartItem) {
            List<FlowerDetailInfo> flowers = cartItem.getDetails().stream()
                    .map(FlowerDetailInfo::from)
                    .collect(Collectors.toList());
            
            int totalFlowerPrice = flowers.stream()
                    .mapToInt(FlowerDetailInfo::getTotalPrice)
                    .sum();
            
            return CartItemInfo.builder()
                    .cartItemId(cartItem.getId())
                    .shopAddress(cartItem.getShop().getRegion().getDescription() + " " + cartItem.getShop().getDistrict().getDescription() + " " + cartItem.getShop().getAddress())
                    .shopId(cartItem.getShop().getId())
                    .shopName(cartItem.getShop().getShopName())
                    .flowers(flowers)
                    .totalFlowerPrice(totalFlowerPrice)
                    .totalPrice(totalFlowerPrice)
                    .createdAt(cartItem.getCreatedAt())
                    .build();
        }
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowerDetailInfo {
        private Long detailId;
        private Long shopFlowerId;
        private String flowerName;
        private Color flowerColor;
        private Integer quantity;
        private Integer basePrice;
        private Integer totalPrice;

        public static FlowerDetailInfo from(CartItemDetail detail) {
            return FlowerDetailInfo.builder()
                    .detailId(detail.getId())
                    .shopFlowerId(detail.getShopFlower().getId())
                    .flowerName(detail.getShopFlower().getFlower().getName())
                    .flowerColor(detail.getFlowerColor())
                    .quantity(detail.getQuantity())
                    .basePrice(detail.getBasePrice())
                    .totalPrice(detail.calculatePrice())
                    .build();
        }
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartCount {
        private Integer count;
        
        public static CartCount of(int count) {
            return CartCount.builder()
                    .count(count)
                    .build();
        }
    }
}
