package com.flowerable.spring.interfaces.admin;

import com.flowerable.spring.application.admin.dto.AdminOrderDetailRes;
import com.flowerable.spring.application.admin.dto.AdminOrderListRes;
import com.flowerable.spring.application.admin.dto.AdminOrderSearchCond;
import com.flowerable.spring.global.dto.PageResponse;
import com.flowerable.spring.application.admin.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public PageResponse<AdminOrderListRes> getOrders(
            AdminOrderSearchCond cond,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return PageResponse.from(adminOrderService.getOrders(cond, pageable));
    }

    @GetMapping("/{orderId}")
    public AdminOrderDetailRes getOrderDetail(@PathVariable Long orderId) {
        return adminOrderService.getOrderDetail(orderId);
    }
}
