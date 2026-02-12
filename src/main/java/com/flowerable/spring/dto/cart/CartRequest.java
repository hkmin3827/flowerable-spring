package com.flowerable.spring.dto.cart;

import com.flowerable.spring.constant.shopflower.Color;
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
        private String wrappingColorName;
        private Integer wrappingExtraPrice;
        private String message;
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
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCartItem {
        private String wrappingColorName;
        private Integer wrappingExtraPrice;
        private String message;
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
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateQuantity {
        private Integer quantity;
    }
}
