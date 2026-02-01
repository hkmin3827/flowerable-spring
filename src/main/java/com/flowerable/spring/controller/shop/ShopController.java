package com.flowerable.spring.controller.shop;

import com.flowerable.spring.dto.shop.ShopDetailRes;
import com.flowerable.spring.dto.shop.ShopSearchRes;
import com.flowerable.spring.dto.shop.ShopUpdateInfoReq;
import com.flowerable.spring.dto.shop.WrappingOptionReq;
import com.flowerable.spring.dto.wrappingoption.WrappingOptionRes;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.shop.ShopService;
import com.flowerable.spring.service.wrappingoption.WrappingOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    private final WrappingOptionService wrappingOptionService;

    @GetMapping("/me")
    public ShopDetailRes me(@AuthenticationPrincipal CustomUserDetails details) {
        return shopService.getMyDetails(details.getId());
    }

    @PatchMapping("/me")
    public void updateShopInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ShopUpdateInfoReq req
    ) {
        shopService.updateShopInfo(userDetails.getId(), req);
    }

    @GetMapping("/{shopId}")
    public ShopDetailRes getShopDetails(@PathVariable Long shopId)
    {
        return shopService.getDetails(shopId);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ShopSearchRes>> searchShops(
            @RequestParam String flowerName,
            @RequestParam(required = false) String regionDesc,
            @RequestParam(required = false) String districtDesc,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<ShopSearchRes> result =
                shopService.searchShops(
                        flowerName,
                        regionDesc,
                        districtDesc,
                        pageable
                );

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }



    @PutMapping("/wrapping-options")
    public ResponseEntity<Void> saveWrappingOptions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody WrappingOptionReq req
    ) {
        wrappingOptionService.saveWrappingOptions(
                userDetails.getId(),
                req.getColorNames(),
                req.getPrice()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wrapping-options")
    public ResponseEntity<WrappingOptionRes> getMyWrappingOptions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                wrappingOptionService.getMyWrappingOption(userDetails.getId())
        );
    }
}
