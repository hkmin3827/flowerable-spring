//package com.flowerable.spring.service.admin;
//
//import com.flowerable.spring.constant.order.OrderCancelBy;
//import com.flowerable.spring.dto.admin.AdminOrderDetailRes;
//import com.flowerable.spring.dto.admin.AdminOrderItemRes;
//import com.flowerable.spring.dto.admin.AdminOrderListRes;
//import com.flowerable.spring.dto.admin.AdminOrderSearchCond;
//import com.flowerable.spring.entity.order.OrderItem;
//import com.flowerable.spring.entity.order.OrderRequest;
//import com.flowerable.spring.exception.CustomException;
//import com.flowerable.spring.constant.common.ErrorCode;
//import com.flowerable.spring.repository.OrderCancelLogRepository;
//import com.flowerable.spring.repository.OrderRequestRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AdminOrderService {
//
//    private final OrderRequestRepository orderRequestRepository;
//    private final OrderCancelLogRepository orderCancelLogRepository;
//
//    @Transactional(readOnly = true)
//    public Page<AdminOrderListRes> getOrders(AdminOrderSearchCond cond, Pageable pageable) {
//
//        Page<OrderRequest> page = orderRequestRepository.findAdminOrders(
//                cond.getStatus(),
//                cond.getUserId(),
//                cond.getShopId(),
//                cond.getFrom(),
//                cond.getTo(),
//                pageable
//        );
//
//        // CANCELLED 주문에 한해서만 canceledBy 조회(최소 비용)
//        return page.map(order -> {
//            OrderCancelBy canceledBy = null;
//            if (order.getStatus() != null && order.getStatus().name().equals("CANCELLED")) {
//                canceledBy = orderCancelLogRepository.findByOrderRequestId(order.getId())
//                        .map(log -> log.getCanceledBy())
//                        .orElse(null);
//            }
//
//            return AdminOrderListRes.builder()
//                    .orderId(order.getId())
//                    .status(order.getStatus())
//                    .userId(order.getUserId())
//                    .shopId(order.getShopId())
//                    .totalFlowerPrice(order.getTotalFlowerPrice())
//                    .totalPrice(order.getTotalPrice())
//                    .createdAt(order.getCreatedAt())
//                    .canceledAt(order.getCanceledAt())
//                    .canceledBy(canceledBy)
//                    .build();
//        });
//    }
//
//    @Transactional(readOnly = true)
//    public AdminOrderDetailRes getOrderDetail(Long orderId) {
//
//        OrderRequest order = orderRequestRepository.findAdminOrderDetail(orderId)
//                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
//
//        OrderCancelBy canceledBy = orderCancelLogRepository.findByOrderRequestId(orderId)
//                .map(log -> log.getCanceledBy())
//                .orElse(null);
//
//        List<AdminOrderItemRes> items = order.getOrderItems().stream()
//                .map(this::toAdminOrderItemRes)
//                .toList();
//
//        return AdminOrderDetailRes.builder()
//                .orderId(order.getId())
//                .status(order.getStatus())
//                .userId(order.getUserId())
//                .shopId(order.getShopId())
//                .totalFlowerPrice(order.getTotalFlowerPrice())
//                .totalPrice(order.getTotalPrice())
//                .wrappingColorName(order.getWrappingColorName())
//                .wrappingExtraPrice(order.getWrappingExtraPrice())
//                .createdAt(order.getCreatedAt())
//                .canceledAt(order.getCanceledAt())
//                .canceledBy(canceledBy)
//                .items(items)
//                .build();
//    }
//
//    private AdminOrderItemRes toAdminOrderItemRes(OrderItem oi) {
//        String flowerName = null;
//        if (oi.getShopFlower() != null && oi.getShopFlower().getFlower() != null) {
//            flowerName = oi.getShopFlower().getFlower().getName();
//        }
//
//        return AdminOrderItemRes.builder()
//                .orderItemId(oi.getId())
//                .shopFlowerId(oi.getShopFlower() != null ? oi.getShopFlower().getId() : null)
//                .flowerName(flowerName)
//                .flowerColor(oi.getFlowerColor())
//                .quantity(oi.getQuantity())
//                .basePrice(oi.getBasePrice())
//                .itemTotalPrice(oi.calculateItemPrice())
//                .build();
//    }
//}