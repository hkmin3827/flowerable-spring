package com.flowerable.spring.domain.order.service;

import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.notification.constant.NotificationReceiverType;
import com.flowerable.spring.domain.notification.constant.NotificationType;
import com.flowerable.spring.domain.order.constant.OrderCancelBy;
import com.flowerable.spring.domain.order.constant.OrderCancelReason;
import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.domain.notification.dto.NotificationCreateReq;
import com.flowerable.spring.domain.order.dto.*;
import com.flowerable.spring.domain.order.entity.OrderCancelLog;
import com.flowerable.spring.domain.order.entity.OrderItem;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.order.repository.OrderCancelLogRepository;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.shop.entity.Shop;
import com.flowerable.spring.domain.shop.repository.ShopRepository;
import com.flowerable.spring.domain.shopflower.entity.ShopFlower;
import com.flowerable.spring.domain.shopflower.repository.ShopFlowerRepository;
import com.flowerable.spring.domain.user.entity.User;
import com.flowerable.spring.domain.user.repository.UserRepository;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import com.flowerable.spring.domain.notification.service.NotificationService;
import com.flowerable.spring.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserOrderService {
    private final OrderRequestRepository orderRequestRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopFlowerRepository shopFlowerRepository;
    private final OrderCancelLogService orderCancelLogService;
    private final NotificationService notificationService;
    private final OrderNumberGenerator orderNumberGenerator;
    private final OrderCancelLogRepository orderCancelLogRepository;
    private final PaymentService paymentService;

    @Transactional
    public OrderCreateRes createOrder(Long accountId, Long shopId, OrderCreateReq req) {
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId).
                orElseThrow(UserNotFoundException::new);

        Shop shop = shopRepository.findByIdAndDeletedAtIsNullAndIsActive(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_ORDER_CREATE_FAIL));

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemReq itemReq : req.getOrderItems()) {

            ShopFlower shopFlower = shopFlowerRepository
                    .findByIdAndShopIdAndOnSaleTrue(itemReq.getShopFlowerId(), shopId)
                    .orElseThrow(() -> new CustomException(
                            ErrorCode.SHOP_FLOWER_NOT_ON_SALE
                    ));

            OrderItem orderItem = OrderItem.create(
                    shopFlower,
                    itemReq.getQuantity(),
                    itemReq.getFlowerColor()
            );

            orderItems.add(orderItem);
        }

        String orderNumber = orderNumberGenerator.generate();

        OrderRequest order = OrderRequest.create(
                orderNumber,
                user,
                shop,
                req.getWrappingColorName(),
                req.getWrappingExtraPrice(),
                orderItems,
                req.getMessage()
        );

        orderRequestRepository.save(order);

        return new OrderCreateRes(order.getId(), order.getOrderNumber(), order.getTotalPrice());
    }

    public void cancelOrder(Long accountId, Long orderId){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);

        OrderRequest order = orderRequestRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(OrderNotFoundException::new);

        if(order.getStatus() != OrderStatus.CANCELED && order.getStatus() != OrderStatus.REQUESTED){
            throw new CustomException(ErrorCode.ORDER_ALREADY_ACCEPTED);
        }

        if(order.getStatus() == OrderStatus.CANCELED) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_CANCELED);
        }

        paymentService.cancelPayment(order, OrderCancelReason.CUSTOMER_REQUEST);

        cancelOrderTransaction(order);
    }

    @Transactional
    protected void cancelOrderTransaction(OrderRequest order) {

        order.cancel();

        notifyShop(
                order,
                order.getShop().getId(),
                NotificationType.ORDER_CANCELED,
                "고객이 주문을 취소하였습니다."
        );

        orderCancelLogService.recordCancel(
                order.getId(),
                OrderCancelBy.USER,
                null
        );
    }

    @Transactional(readOnly = true)
    public Page<OrderListRes> getMyOrders(Long accountId, Pageable pageable){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);

        return orderRequestRepository.findUserOrders(user.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public OrderDetailRes getOrderDetails(Long accountId, Long orderId){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);

        OrderRequest order = orderRequestRepository.findUserOrderDetails(user.getId(), orderId)
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
                            .flowerColor(i.getFlowerColor())
                            .flowerBasePrice(i.getBasePrice())
                            .quantity(i.getQuantity())
                            .itemTotalPrice(i.calculateItemPrice())
                            .build();
                })
                .toList();

        return OrderDetailRes.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .opponentTelnum(order.getShop().getAccount().getTelnum())
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
                .cancelReason(cancelReason)
                .cancelBy(canceledBy)
                .build();
    }

    private void notifyShop(OrderRequest order, Long receiverId, NotificationType type, String content) {
        notificationService.createNotification(
                new NotificationCreateReq(
                        NotificationReceiverType.SHOP,
                        receiverId,
                        type,
                        order.getOrderNumber() + " : " + type.getTitle(),
                        content,
                        order.getId()
                )
        );
    }
}
