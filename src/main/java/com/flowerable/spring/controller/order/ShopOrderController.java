package com.flowerable.spring.controller.order;

import com.flowerable.spring.constant.order.OrderStatus;
import com.flowerable.spring.dto.common.PageResponse;
import com.flowerable.spring.dto.order.OrderDetailRes;
import com.flowerable.spring.dto.order.OrderListRes;
import com.flowerable.spring.dto.order.OrderStatusChangeReq;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.order.ShopOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
