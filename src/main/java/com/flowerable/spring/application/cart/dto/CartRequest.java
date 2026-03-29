package com.flowerable.spring.application.cart.dto;

import com.flowerable.spring.domain.shopflower.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CartRequest {
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToCart {
        private Long shopId;
        private List<FlowerItem> flowers;
        
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class FlowerItem {
            private Long shopFlowerId;
            private Integer quantity;
            private Color flowerColor;
        }
    }
}
