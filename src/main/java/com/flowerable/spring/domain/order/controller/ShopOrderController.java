package com.flowerable.spring.domain.order.controller;

import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.global.dto.PageResponse;
import com.flowerable.spring.domain.order.dto.OrderDetailRes;
import com.flowerable.spring.domain.order.dto.OrderListRes;
import com.flowerable.spring.domain.order.dto.OrderStatusChangeReq;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.domain.order.service.ShopOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/shops")
@RequiredArgsConstructor
public class ShopOrderController {
    private final ShopOrderService shopOrderService;

    @PatchMapping("/{orderRequestId}")
    public ResponseEntity<Void> changeOrderStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderRequestId,
            @RequestBody OrderStatusChangeReq req
            ){
        shopOrderService.changeStatus(userDetails.getId(), orderRequestId, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse<OrderListRes> getMyShopOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable
    ) {
        return PageResponse.from(shopOrderService.getMyShopOrders(userDetails.getId(), status, pageable));
    }

    @GetMapping("/{orderId}")
    public OrderDetailRes getOrderDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId
    ){
        return shopOrderService.getOrderDetails(userDetails.getId(), orderId);
    }

    @GetMapping("/dashboard/recent-requests")
    public PageResponse<OrderListRes> getRecentRequestedOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        return PageResponse.from(shopOrderService.getRecentRequestedOrders(userDetails.getId(), pageable));
    }
}
