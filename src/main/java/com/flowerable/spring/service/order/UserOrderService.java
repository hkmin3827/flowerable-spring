package com.flowerable.spring.service.order;

import com.flowerable.spring.constant.*;
import com.flowerable.spring.dto.notification.NotificationCreateReq;
import com.flowerable.spring.dto.order.*;
import com.flowerable.spring.entity.order.OrderItem;
import com.flowerable.spring.entity.order.OrderRequest;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import com.flowerable.spring.entity.user.User;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.OrderNotFoundException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.repository.OrderRequestRepository;
import com.flowerable.spring.repository.ShopFlowerRepository;
import com.flowerable.spring.repository.ShopRepository;
import com.flowerable.spring.repository.UserRepository;
import com.flowerable.spring.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOrderService {
    private final OrderRequestRepository orderRequestRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopFlowerRepository shopFlowerRepository;
    private final OrderCancelLogService orderCancelLogService;
    private final NotificationService notificationService;

    @Transactional
    public Long createOrder(Long userId, Long shopId, OrderCreateReq req) {
        if(!userRepository.existsActiveUser(userId) || !shopRepository.existsActiveShop(shopId)){
            throw new CustomException(ErrorCode.SUSPEND_ORDER_ACCOUNT);
        }

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

        OrderRequest order = OrderRequest.create(
                userId,
                shopId,
                req.getWrappingColorName(),
                req.getWrappingExtraPrice(),
                orderItems,
                req.getMessage()
        );

        orderRequestRepository.save(order);

        String content = order.getMessage() == null
                ? "주문 확인 후 접수 또는 취소해주세요."
                : "주문 확인 후 접수 또는 취소해주세요. (요청 사항 : " + order.getMessage()+ ")";
        notifyShop(order, order.getShopId(), NotificationType.ORDER_CREATED, "새 주문이 접수되었습니다.", content);
        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long accountId, Long orderId){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);

        OrderRequest order = orderRequestRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(OrderNotFoundException::new);

        if(order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.REQUESTED){
            throw new CustomException(ErrorCode.ORDER_ALREADY_ACCEPTED);
        }

        if(order.getStatus() == OrderStatus.CANCELLED) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_CANCELED);
        }
        order.cancel();

        notifyShop(order, order.getShopId(), NotificationType.ORDER_CANCELED, "주문이 취소되었습니다.", "고객이 주문을 취소하였습니다.");
        orderCancelLogService.recordCancel(order.getId(), OrderCancelBy.USER);
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

        List<OrderItemRes> items = order.getOrderItems().stream()
                .map(i -> {

                    return OrderItemRes.builder()
                            .shopFlowerId(i.getShopFlower().getId())
                            .flowerName(i.getShopFlower().getFlower().getName())
                            .flowerColor(i.getFlowerColor())
                            .quantity(i.getQuantity())
                            .itemTotalPrice(i.calculateItemPrice())
                            .build();
                })
                .toList();

        return OrderDetailRes.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalFlowerPrice(order.getTotalFlowerPrice())
                .wrappingExtraPrice(order.getWrappingExtraPrice())
                .totalPrice(order.getTotalPrice())
                .wrappingColorName(order.getWrappingColorName())
                .createdAt(order.getCreatedAt())
                .canceledAt(order.getCanceledAt())
                .items(items)
                .message(order.getMessage())
                .build();
    }

    private void notifyShop(OrderRequest order, Long receiverId, NotificationType type, String title, String content) {
        notificationService.createNotification(
                new NotificationCreateReq(
                        NotificationReceiverType.SHOP,
                        receiverId,
                        type,
                        title,
                        content,
                        order.getId()
                )
        );
    }
}
