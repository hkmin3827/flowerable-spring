package com.flowerable.spring.domain.shopflower.controller;

import com.flowerable.spring.global.dto.PageResponse;
import com.flowerable.spring.domain.shopflower.dto.ShopFlowerOrderStatsRes;
import com.flowerable.spring.domain.shopflower.dto.ShopFlowerRegReq;
import com.flowerable.spring.domain.shopflower.dto.ShopFlowerRes;
import com.flowerable.spring.domain.shopflower.dto.ShopFlowerUpdateReq;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.domain.shopflower.service.ShopFlowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopflowers")
@RequiredArgsConstructor
public class ShopFlowerController {
    private final ShopFlowerService shopFlowerService;

    @PostMapping("/register")
    public void registerShopFlower(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ShopFlowerRegReq req
            ){
        shopFlowerService.register(userDetails.getId(), req);
    }

    @PatchMapping("/update/{shopFlowerId}")
    public ResponseEntity<Void> updateOption(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long shopFlowerId,
            @Valid @RequestBody ShopFlowerUpdateReq req
            ){
        shopFlowerService.updateOption(userDetails.getId(), shopFlowerId, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse
            <ShopFlowerRes> getMyShopFlowers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Boolean onSale,
            Pageable pageable
    ) {
        return PageResponse.from(shopFlowerService.getMyShopFlowers(userDetails.getId(), onSale, pageable));
    }
    @PatchMapping("/activate/{shopFlowerId}")
    public ResponseEntity<Void> activateShopFlower(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long shopFlowerId
    ){
        shopFlowerService.activate(userDetails.getId(), shopFlowerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deactivate/{shopFlowerId}")
    public ResponseEntity<Void> deactivateShopFlower(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long shopFlowerId
    ){
        shopFlowerService.deactivate(userDetails.getId(), shopFlowerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard/top-flowers")
    public ResponseEntity<List<ShopFlowerOrderStatsRes>> getTop5FlowerStats(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(shopFlowerService.getTop5FlowerStats(userDetails.getId()));
    }

}