package com.flowerable.spring.service.order;

import com.flowerable.spring.constant.ErrorCode;
import com.flowerable.spring.constant.OrderCancelBy;
import com.flowerable.spring.constant.OrderStatus;
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

    @Transactional
    public Long createOrder(Long userId, Long shopId, OrderCreateReq req) {
        if(!userRepository.existsActiveUser(userId) || !shopRepository.existsActiveShop(shopId)){
            throw new CustomException(ErrorCode.SUSPEND_ORDER_ACCOUNT);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemReq itemReq : req.getOrderItems()) {

            ShopFlower shopFlower = shopFlowerRepository
                    .findByIdAndOnSaleTrue(itemReq.getShopFlowerId())
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
                orderItems
        );

        orderRequestRepository.save(order);
        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long accountId, Long orderId){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);

        OrderRequest order = orderRequestRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(OrderNotFoundException::new);

        if(order.getStatus() == OrderStatus.CANCELLED || order.getCanceledAt() != null) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_CANCELED);
        }

        order.cancel();
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
                .build();
    }
}
