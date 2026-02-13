package com.flowerable.spring.controller.admin;

import com.flowerable.spring.constant.shop.ShopStatus;
import com.flowerable.spring.dto.admin.AdminShopListRes;
import com.flowerable.spring.dto.common.PageResponse;
import com.flowerable.spring.dto.shop.ShopDetailRes;
import com.flowerable.spring.service.admin.AdminShopService;
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
    public PageResponse<AdminShopListRes> getShopsByStatus(
            @RequestParam(required = false) ShopStatus status,
            @PageableDefault(size = 20, sort = "id")
            Pageable pageable
    ) {
        return PageResponse.from(adminShopService.getShopsByStatus(status, pageable));
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

    // activate와 의미 동일하지만 재활성화, 승인 호출 api 구분 (의도 분리)
    @PatchMapping("/{shopId}/approve")
    public ResponseEntity<Void> approveShop(
            @PathVariable Long shopId
    ){
        adminShopService.changeStatus(shopId, ShopStatus.ACTIVE);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{shopId}/activate")
    public ResponseEntity<Void> activateShop(
            @PathVariable Long shopId
    ){
        adminShopService.changeStatus(shopId, ShopStatus.ACTIVE);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{shopId}/suspend")
    public ResponseEntity<Void> suspendShop(
            @PathVariable Long shopId
    ){
        adminShopService.changeStatus(shopId, ShopStatus.SUSPENDED);
        return ResponseEntity.noContent().build();
    }
}
