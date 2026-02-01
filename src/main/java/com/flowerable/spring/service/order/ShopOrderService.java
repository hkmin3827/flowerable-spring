package com.flowerable.spring.service.order;

import com.flowerable.spring.constant.ErrorCode;
import com.flowerable.spring.constant.OrderCancelBy;
import com.flowerable.spring.constant.OrderStatus;
import com.flowerable.spring.dto.order.OrderItemRes;
import com.flowerable.spring.dto.order.OrderDetailRes;
import com.flowerable.spring.dto.order.OrderListRes;
import com.flowerable.spring.entity.order.OrderRequest;
import com.flowerable.spring.dto.order.OrderStatusChangeReq;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.OrderNotFoundException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.repository.OrderRequestRepository;
import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopOrderService {
    private final OrderRequestRepository orderRequestRepository;
    private final ShopRepository shopRepository;
    private final OrderCancelLogService orderCancelLogService;

    @Transactional
    public void changeStatus(Long accountId, Long orderId, OrderStatusChangeReq req) {
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(ShopNotFoundException::new);

        OrderRequest orderReq = orderRequestRepository.findDetailForStatusChange(orderId, shop.getId())
                .orElseThrow(OrderNotFoundException::new);

        OrderStatus current = orderReq.getStatus();

        validateTransition(current, req.status());

        orderReq.changeStatus(req.status());

        if(req.status() == OrderStatus.CANCELLED){
            orderReq.markCanceledAt();
            orderCancelLogService.recordCancel(orderReq.getId(), OrderCancelBy.SHOP);
        }
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


    private void validateTransition(OrderStatus current, OrderStatus statusReq) {
        switch (current) {
            case REQUESTED -> {
                if (statusReq != OrderStatus.CONFIRMED && statusReq != OrderStatus.CANCELLED) {
                    throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
                }
            }
            case CONFIRMED -> {
                if (statusReq != OrderStatus.READY && statusReq != OrderStatus.CANCELLED) {
                    throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
                }
            }
            case READY -> {
                if (statusReq != OrderStatus.COMPLETED) {
                    throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
                }
            }
            case COMPLETED, CANCELLED -> {
                throw new CustomException(ErrorCode.FAIL_CHANGE_ORDER_STATUS);
            }
            default -> throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }
}
