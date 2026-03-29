package com.flowerable.spring.interfaces.admin;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import com.flowerable.spring.application.admin.dto.AdminShopListRes;
import com.flowerable.spring.global.dto.PageResponse;
import com.flowerable.spring.application.shop.dto.ShopDetailRes;
import com.flowerable.spring.application.admin.AdminShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shops")
@RequiredArgsConstructor
public class AdminShopController {
    private final AdminShopService adminShopService;

    @GetMapping
    public PageResponse<AdminShopListRes> getShops(
            @RequestParam(required = false) ShopStatus shopStatus,
            @RequestParam(required = false) AccountStatus accountStatus,
            Pageable pageable
    ) {
        return PageResponse.from(
                adminShopService.getShops(shopStatus, accountStatus, pageable)
        );
    }

    @GetMapping("/search")
    public PageResponse<AdminShopListRes> searchShops(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "id")
            Pageable pageable
    ) {
        return PageResponse.from(adminShopService.searchShops(keyword, pageable));
    }

    @GetMapping("/{shopId}")
    public ShopDetailRes getShopDetails(@PathVariable Long shopId)
    {
        return adminShopService.getShopDetails(shopId);
    }


    @PatchMapping("/{shopId}/activate-account")
    public ResponseEntity<Void> activateShopAccount(
            @PathVariable Long shopId
    ){
        adminShopService.changeStatus(shopId, AccountStatus.ACTIVE);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shopId}/suspend-account")
    public ResponseEntity<Void> suspendShopAccount(
            @PathVariable Long shopId
    ){
        adminShopService.changeStatus(shopId, AccountStatus.SUSPENDED);

        return ResponseEntity.noContent().build();
    }

    // activate와 의미 동일하지만 재활성화, 승인 호출 api 구분 (의도 분리)
    @PatchMapping("/{shopId}/approve")
    public ResponseEntity<Void> approveShop(
            @PathVariable Long shopId
    ){
        adminShopService.changeShopStatus(shopId, ShopStatus.ACTIVE);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shopId}/reject")
    public ResponseEntity<Void> rejectShop(
            @PathVariable Long shopId
    ){
        adminShopService.changeShopStatus(shopId, ShopStatus.REJECTED);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{shopId}/activate")
    public ResponseEntity<Void> activateShop(
            @PathVariable Long shopId
    ){
        adminShopService.changeShopStatus(shopId, ShopStatus.ACTIVE);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{shopId}/suspend")
    public ResponseEntity<Void> suspendShop(
            @PathVariable Long shopId
    ){
        adminShopService.changeShopStatus(shopId, ShopStatus.SUSPENDED);
        return ResponseEntity.noContent().build();
    }
}
