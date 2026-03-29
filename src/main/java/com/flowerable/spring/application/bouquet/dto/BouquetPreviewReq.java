package com.flowerable.spring.application.bouquet.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class BouquetPreviewReq {
    private List<PreviewItemReq> orderItems;
    private String wrappingColorName;

    @Getter
    public static class PreviewItemReq {
        private String flowerName;
        private String flowerColor;
        private int quantity;
    }
}
