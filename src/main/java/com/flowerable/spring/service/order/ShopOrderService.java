package com.flowerable.spring.service.order;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.notification.NotificationReceiverType;
import com.flowerable.spring.constant.notification.NotificationType;
import com.flowerable.spring.constant.order.OrderCancelBy;
import com.flowerable.spring.constant.order.OrderCancelReason;
import com.flowerable.spring.constant.order.OrderStatus;
import com.flowerable.spring.dto.notification.NotificationCreateReq;
import com.flowerable.spring.dto.order.*;
import com.flowerable.spring.entity.order.OrderCancelLog;
import com.flowerable.spring.entity.order.OrderRequest;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.OrderNotFoundException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.repository.OrderCancelLogRepository;
import com.flowerable.spring.repository.OrderRequestRepository;
import com.flowerable.spring.repository.ShopRepository;
import com.flowerable.spring.service.notification.NotificationService;
import com.flowerable.spring.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopOrderService {
    private final OrderRequestRepository orderRequestRepository;
    private final ShopRepository shopRepository;
    private final OrderCancelLogService orderCancelLogService;
    private final NotificationService notificationService;
    private final OrderCancelLogRepository orderCancelLogRepository;
    private final PaymentService paymentService;

    @Transactional
    public void changeStatus(Long accountId, Long orderId, OrderStatusChangeReq req) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        OrderRequest orderReq = orderRequestRepository.findDetailForStatusChange(orderId, shop.getId())
                .orElseThrow(OrderNotFoundException::new);

        OrderStatus targetStatus = req.status();
        OrderStatus current = orderReq.getStatus();

        validateTransition(current, targetStatus);

        if(targetStatus == OrderStatus.CANCELED){
            if (req.cancelReason() == null) {
                throw new CustomException(ErrorCode.CANCEL_REASON_REQUIRED);
            }

            paymentService.cancelPayment(orderReq, req.cancelReason());
            orderReq.markCanceledAt();
            orderCancelLogService.recordCancel(orderReq.getId(), OrderCancelBy.SHOP, req.cancelReason());
            notifyUser(orderReq, orderReq.getUser().getId(), NotificationType.ORDER_CANCELED,  "취소 사유 : " + req.cancelReason().getDescription());
        }
        if(targetStatus == OrderStatus.ACCEPTED){
            notifyUser(orderReq, orderReq.getUser().getId(), NotificationType.ORDER_ACCEPTED, "주문해주셔서 감사합니다. 빠르게 준비해드리겠습니다.");
        }
        if(targetStatus == OrderStatus.READY){
            notifyUser(orderReq, orderReq.getUser().getId(), NotificationType.ORDER_READY, "매장으로 픽업하러 방문해주세요.");
        }

        orderReq.changeStatus(targetStatus);
    }

    @Transactional(readOnly = true)
    public Page<OrderListRes> getMyShopOrders(Long accountId, OrderStatus status, Pageable pageable){
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        return orderRequestRepository.findShopOrders(shop.getId(), status, pageable);
    }

    @Transactional(readOnly = true)
    public OrderDetailRes getOrderDetails(Long accountId, Long orderId){
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        OrderRequest order = orderRequestRepository.findShopOrderDetails(shop.getId(), orderId)
                .orElseThrow(OrderNotFoundException::new);

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
            }
            if (cancelLog.getCancelReason() != null) {
                cancelReason = cancelLog.getCancelReason().getDescription();
            }
        }

        List<OrderItemRes> items = order.getOrderItems().stream()
                .map(i -> {

                    return OrderItemRes.builder()
                            .shopFlowerId(i.getShopFlower().getId())
                            .flowerName(i.getShopFlower().getFlower().getName())
                            .flowerBasePrice(i.getBasePrice())
                            .flowerColor(i.getFlowerColor())
                            .quantity(i.getQuantity())
                            .itemTotalPrice(i.calculateItemPrice())
                            .build();
                })
                .toList();


        return OrderDetailRes.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .opponentTelnum(order.getUser().getAccount().getTelnum())
                .shopAddress(order.getShop().getRegion().getDescription() + " " + order.getShop().getDistrict().getDescription() + " " + order.getShop().getAddress())
                .status(order.getStatus())
                .totalFlowerPrice(order.getTotalFlowerPrice())
                .wrappingExtraPrice(order.getWrappingExtraPrice())
                .totalPrice(order.getTotalPrice())
                .wrappingColorName(order.getWrappingColorName())
                .createdAt(order.getCreatedAt())
                .canceledAt(order.getCanceledAt())
                .items(items)
                .message(order.getMessage())
                .userId(order.getUser().getId())
                .shopId(order.getShop().getId())
                .shopName(order.getShop().getShopName())
                .userName(order.getUser().getName())
                .cancelBy(canceledBy)
                .cancelReason(cancelReason)
                .build();
    }
    /**
     * Shop 대시보드: REQUESTED 상태 최신 8개 주문 조회
     */
    @Transactional(readOnly = true)
    public Page<OrderListRes> getRecentRequestedOrders(Long accountId, Pageable pageable) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        return orderRequestRepository.findRecentRequestedOrders(shop.getId(), PageRequest.of(0, 5));
    }


    private void validateTransition(OrderStatus current, OrderStatus statusReq) {
        switch (current) {
            case REQUESTED -> {
                if (statusReq != OrderStatus.ACCEPTED && statusReq != OrderStatus.CANCELED) {
                    throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
                }
            }
            case ACCEPTED -> {
                if (statusReq != OrderStatus.READY && statusReq != OrderStatus.CANCELED) {
                    throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
                }
            }
            case READY -> {
                if (statusReq != OrderStatus.COMPLETED) {
                    throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
                }
            }
            case COMPLETED, CANCELED -> {
                throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
            }
            default -> throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }
    private void notifyUser(OrderRequest order, Long receiverId, NotificationType type, String content) {
        notificationService.createNotification(
                new NotificationCreateReq(
                        NotificationReceiverType.USER,
                        receiverId,
                        type,
                        type.getTitle(),
                        content,
                        order.getId()
                )
        );
    }


}
