//package com.flowerable.spring.controller.admin;
//
//import com.flowerable.spring.dto.admin.AdminOrderDetailRes;
//import com.flowerable.spring.dto.admin.AdminOrderListRes;
//import com.flowerable.spring.dto.admin.AdminOrderSearchCond;
//import com.flowerable.spring.dto.common.PageResponse;
//import com.flowerable.spring.service.admin.AdminOrderService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/admin/orders")
//public class AdminOrderController {
//
//    private final AdminOrderService adminOrderService;
//
//    /**
//     * 관리자 주문 리스트(모니터링)
//     * 예: /api/admin/orders?status=CANCELLED&shopId=3&from=2026-02-01T00:00:00
//     */
//    @GetMapping
//    public PageResponse<AdminOrderListRes> getOrders(
//            AdminOrderSearchCond cond,
//            @PageableDefault(size = 20) Pageable pageable
//    ) {
//        return PageResponse.from(adminOrderService.getOrders(cond, pageable));
//    }
//
//    /**
//     * 관리자 주문 상세(아이템 포함)
//     */
//    @GetMapping("/{orderId}")
//    public AdminOrderDetailRes getOrderDetail(@PathVariable Long orderId) {
//        return adminOrderService.getOrderDetail(orderId);
//    }
//}