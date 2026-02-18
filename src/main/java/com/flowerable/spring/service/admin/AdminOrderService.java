package com.flowerable.spring.service.admin;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.order.OrderCancelBy;
import com.flowerable.spring.constant.order.OrderCancelReason;
import com.flowerable.spring.constant.order.OrderStatus;
import com.flowerable.spring.dto.admin.AdminOrderDetailRes;
import com.flowerable.spring.dto.admin.AdminOrderItemRes;
import com.flowerable.spring.dto.admin.AdminOrderListRes;
import com.flowerable.spring.dto.admin.AdminOrderSearchCond;
import com.flowerable.spring.entity.order.OrderCancelLog;
import com.flowerable.spring.entity.order.OrderItem;
import com.flowerable.spring.entity.order.OrderRequest;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.repository.OrderCancelLogRepository;
import com.flowerable.spring.repository.OrderRequestRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRequestRepository orderRequestRepository;
    private final OrderCancelLogRepository orderCancelLogRepository;

    /**
     * 관리자 주문 목록 조회 (검색 조건), 동적 쿼리
     */
    @Transactional(readOnly = true)
    public Page<AdminOrderListRes> getOrders(AdminOrderSearchCond cond, Pageable pageable) {
        Specification<OrderRequest> spec = (root, query, cb) -> {

            // fetch join (N+1 방지)
            root.fetch("shop", JoinType.INNER);
            root.fetch("user", JoinType.INNER);

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

        Page<OrderRequest> orderPage =
                orderRequestRepository.findAll(spec, pageable);

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

    /**
     * 관리자 주문 상세 조회
     */
    @Transactional(readOnly = true)
    public AdminOrderDetailRes getOrderDetail(Long orderId) {
        OrderRequest order = orderRequestRepository.findByIdWithItems(orderId);

        if (order == null) {
            throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 취소 로그 조회
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

    /**
     * OrderItem -> AdminOrderItemRes 변환
     */
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
