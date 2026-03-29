package com.flowerable.spring.interfaces.shop;

import com.flowerable.spring.application.shop.dto.ShopImageRes;
import com.flowerable.spring.application.shop.ShopImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shopimages/{shopId}")
public class ShopImageQueryController {
    private final ShopImageService shopImageService;

    @GetMapping
    public List<ShopImageRes> getShopImages(
            @PathVariable Long shopId,
            @RequestParam(required = false) Long lastId
    ) {
        return shopImageService.getShopImages(shopId, lastId);
    }

    @GetMapping("/latest")
    public List<ShopImageRes> getLatestImages(@PathVariable Long shopId) {
        return shopImageService.getLatestImages(shopId);
    }

    @GetMapping("/thumbnail")
    public ResponseEntity<ShopImageRes> getThumbnail(@PathVariable Long shopId) {
        ShopImageRes res = shopImageService.getThumbnail(shopId);
        return res == null
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(res);
    }
}