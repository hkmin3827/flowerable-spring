package com.flowerable.spring.domain.shop.controller;


import com.flowerable.spring.domain.shop.dto.ShopImageCreateReq;
import com.flowerable.spring.domain.shop.dto.ShopImageRes;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.domain.shop.service.ShopImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-shop/images")
public class ShopImageController {

    private final ShopImageService shopImageService;

    @PostMapping
    public ResponseEntity<Void> uploadImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ShopImageCreateReq req
    ) {
        shopImageService.uploadImages(userDetails.getId(), req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long imageId
    ) {
        shopImageService.deleteImage(userDetails.getId(), imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<ShopImageRes> getMyShopImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long lastId
    ) {
        return shopImageService.getMyShopImages(userDetails.getId(), lastId);
    }

    @GetMapping("/latest")
    public List<ShopImageRes> getMyLatestImages(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return shopImageService.getMyLatestImages(userDetails.getId());
    }

    @GetMapping("/thumbnail")
    public ResponseEntity<ShopImageRes> getMyThumbnail(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ShopImageRes res = shopImageService.getMyThumbnail(userDetails.getId());

        if (res == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/thumbnail/{shopImageId}/register")
    public ResponseEntity<Void> registerThumbnail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long shopImageId
    ) {
        shopImageService.registerThumbnail(userDetails.getId(), shopImageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/thumbnail/{shopImageId}/delete")
    public ResponseEntity<Void> deleteThumbnail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long shopImageId
    ) {
        shopImageService.clearThumbnail(userDetails.getId(), shopImageId);
        return ResponseEntity.noContent().build();
    }

}