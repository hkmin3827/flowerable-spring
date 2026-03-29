package com.flowerable.spring.interfaces.order;

import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.application.bouquet.dto.BouquetPreviewReq;
import com.flowerable.spring.application.bouquet.dto.BouquetPreviewRes;
import com.flowerable.spring.global.dto.PageResponse;
import com.flowerable.spring.application.order.dto.OrderCreateReq;
import com.flowerable.spring.application.order.dto.OrderCreateRes;
import com.flowerable.spring.application.order.dto.OrderDetailRes;
import com.flowerable.spring.application.order.dto.OrderListRes;
import com.flowerable.spring.application.wrappingoption.WrappingOptionRes;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.application.bouquet.BouquetPreviewService;
import com.flowerable.spring.application.order.UserOrderService;
import com.flowerable.spring.application.wrappingoption.WrappingOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/users")
@RequiredArgsConstructor
public class UserOrderController {
    private final UserOrderService userOrderService;
    private final WrappingOptionService wrappingOptionService;
    private final BouquetPreviewService bouquetPreviewService;

    @PostMapping("/{shopId}")
    public ResponseEntity<OrderCreateRes> createOrder(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderCreateReq req
    ) {
            OrderCreateRes res= userOrderService.createOrder(
                userDetails.getId(),
                shopId,
                req
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{shopId}/wrapping-options")
    public ResponseEntity<WrappingOptionRes> getShopWrappingOptions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long shopId
    ) {
        return ResponseEntity.ok(
                wrappingOptionService.getShopWrappingOption(shopId)
        );
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId
    ){
        userOrderService.cancelOrder(userDetails.getId(), orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse<OrderListRes> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable
    ) {
        return PageResponse.from(userOrderService.getMyOrders(userDetails.getId(), pageable));
    }

    @GetMapping("/{orderId}")
    public OrderDetailRes getOrderDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId
    ){
        return userOrderService.getOrderDetails(userDetails.getId(), orderId);
    }

    @PostMapping("/{shopId}/bouquet-preview")
    public ResponseEntity<BouquetPreviewRes> generateBouquetPreview(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BouquetPreviewReq req
    ) {
        String imageUrl = bouquetPreviewService.generatePreviewFromReq(req);
        return ResponseEntity.ok(new BouquetPreviewRes(imageUrl));
    }
}