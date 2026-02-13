package com.flowerable.spring.controller.order;

import com.flowerable.spring.constant.order.OrderStatus;
import com.flowerable.spring.dto.common.PageResponse;
import com.flowerable.spring.dto.order.OrderCreateReq;
import com.flowerable.spring.dto.order.OrderDetailRes;
import com.flowerable.spring.dto.order.OrderListRes;
import com.flowerable.spring.dto.wrappingoption.WrappingOptionRes;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.order.UserOrderService;
import com.flowerable.spring.service.wrappingoption.WrappingOptionService;
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

    @PostMapping("/{shopId}")
    public ResponseEntity<Long> createOrder(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderCreateReq req
    ) {
            Long orderId = userOrderService.createOrder(
                userDetails.getId(),
                shopId,
                req
        );

        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/{shopId}/wrapping-options")
    public ResponseEntity<WrappingOptionRes> getShopWrappingOptions(
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
}
