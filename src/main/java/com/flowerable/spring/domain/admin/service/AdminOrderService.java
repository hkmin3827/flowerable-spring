package com.flowerable.spring.domain.admin.service;

import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.order.constant.OrderCancelBy;
import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.domain.admin.dto.AdminOrderDetailRes;
import com.flowerable.spring.domain.admin.dto.AdminOrderItemRes;
import com.flowerable.spring.domain.admin.dto.AdminOrderListRes;
import com.flowerable.spring.domain.admin.dto.AdminOrderSearchCond;
import com.flowerable.spring.domain.order.entity.OrderCancelLog;
import com.flowerable.spring.domain.order.entity.OrderItem;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.domain.order.repository.OrderCancelLogRepository;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRequestRepository orderRequestRepository;
    private final OrderCancelLogRepository orderCancelLogRepository;

    @Transactional(readOnly = true)
    public Page<AdminOrderListRes> getOrders(AdminOrderSearchCond cond, Pageable pageable) {
        Specification<OrderRequest> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (cond.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), cond.getStatus()));
            }

            if (cond.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), cond.getUserId()));
            }

            if (cond.getShopId() != null) {
                predicates.add(cb.equal(root.get("shop").get("id"), cond.getShopId()));
            }

            if (cond.getFrom() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                cond.getFrom()
                        )
                );
            }

            if (cond.getTo() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                cond.getTo()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending() // 최신순 정렬 추가
        );

        Page<OrderRequest> orderPage =
                orderRequestRepository.findAll(spec, sortedPageable);

        return orderPage.map(order -> {

            OrderCancelBy canceledBy = null;

            if (order.getStatus() == OrderStatus.CANCELED) {
                Optional<OrderCancelLog> cancelLogOpt =
                        orderCancelLogRepository.findByOrderRequestId(order.getId());

                if (cancelLogOpt.isPresent()) {
                    OrderCancelLog cancelLog = cancelLogOpt.get();
                    canceledBy = cancelLog.getCanceledBy();
                }
            }

            return AdminOrderListRes.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .shopName(order.getShop().getShopName())
                    .userName(order.getUser().getName())
                    .status(order.getStatus())
                    .totalPrice(order.getTotalPrice())
                    .createdAt(order.getCreatedAt())
                    .canceledAt(order.getCanceledAt())
                    .canceledBy(canceledBy)
                    .build();
        });
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailRes getOrderDetail(Long orderId) {
        OrderRequest order = orderRequestRepository.findByIdWithItems(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (order == null) {
            throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
        }

        String canceledBy = null;
        String cancelReason = null;

        Optional<OrderCancelLog> cancelLogOpt =
                orderCancelLogRepository.findByOrderRequestId(orderId);

        if (cancelLogOpt.isPresent()) {
            OrderCancelLog cancelLog = cancelLogOpt.get();
            if(cancelLog.getCanceledBy() == OrderCancelBy.SHOP) {
                canceledBy = "가게";
            } else {
                canceledBy = "고객";
            }            if (cancelLog.getCancelReason() != null) {
                cancelReason = cancelLog.getCancelReason().getDescription();
            }
        }

        List<AdminOrderItemRes> items = order.getOrderItems().stream()
                .map(this::toAdminOrderItemRes)
                .toList();

        return AdminOrderDetailRes.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .shopId(order.getShop().getId())
                .shopTelnum(order.getShop().getAccount().getTelnum())
                .userTelnum(order.getUser().getAccount().getTelnum())
                .shopName(order.getShop().getShopName())
                .userName(order.getUser().getName())
                .status(order.getStatus())
                .totalFlowerPrice(order.getTotalFlowerPrice())
                .totalPrice(order.getTotalPrice())
                .wrappingColorName(order.getWrappingColorName())
                .wrappingExtraPrice(order.getWrappingExtraPrice())
                .createdAt(order.getCreatedAt())
                .canceledAt(order.getCanceledAt())
                .cancelBy(canceledBy)
                .cancelReason(cancelReason)
                .items(items)
                .build();
    }

    private AdminOrderItemRes toAdminOrderItemRes(OrderItem orderItem) {
        String flowerName = null;

        if (orderItem.getShopFlower() != null &&
                orderItem.getShopFlower().getFlower() != null) {
            flowerName = orderItem.getShopFlower().getFlower().getName();
        }

        return AdminOrderItemRes.builder()
                .orderItemId(orderItem.getId())
                .shopFlowerId(orderItem.getShopFlower() != null ?
                        orderItem.getShopFlower().getId() : null)
                .flowerName(flowerName)
                .flowerColor(orderItem.getFlowerColor())
                .quantity(orderItem.getQuantity())
                .basePrice(orderItem.getBasePrice())
                .itemTotalPrice(orderItem.calculateItemPrice())
                .build();
    }
}
