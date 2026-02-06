package com.flowerable.spring.entity.order;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.order.OrderStatus;
import com.flowerable.spring.exception.CustomException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_requests")
@Getter
@NoArgsConstructor
public class OrderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long shopId;

    @Column(nullable = false)
    private Integer totalFlowerPrice;   // 각 orderItem calculateItemPrice의 합

    @Column(nullable = false)
    private Integer totalPrice;

    private String wrappingColorName;

    private Integer wrappingExtraPrice;

    @Column(nullable = true, length = 100)
    private String message;  // 요청 사항

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime canceledAt;

    @OneToMany(mappedBy = "orderRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static OrderRequest create(Long userId,
                                      Long shopId,
                                      String wrappingColorName,
                                      Integer wrappingExtraPrice,
                                      List<OrderItem> orderItems,
                                      String message) {
        OrderRequest order = new OrderRequest();
        order.userId = userId;
        order.shopId = shopId;
        order.status = OrderStatus.REQUESTED;
        // 포장 옵션 (스냅샷)
        order.wrappingColorName = wrappingColorName;
        order.wrappingExtraPrice = wrappingExtraPrice;

        int totalFlowerPrice = 0;
        for (OrderItem item : orderItems) {
            item.assignOrder(order);
            order.orderItems.add(item);
            totalFlowerPrice += item.calculateItemPrice();
        }
        order.totalFlowerPrice = totalFlowerPrice;

        // 총액 계산
        int wrappingPrice = order.wrappingExtraPrice != null
                ? order.wrappingExtraPrice
                : 0;
        order.totalPrice = order.totalFlowerPrice + wrappingPrice;
        if(message != null){
            order.message = message;
        }
        return order;
    }


    public void markCanceledAt() {
        this.canceledAt = LocalDateTime.now();
    }

    public void cancel() {
        validateStatus(OrderStatus.REQUESTED);
        this.status = OrderStatus.CANCELLED;
        markCanceledAt();
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    private void validateStatus(OrderStatus... allowed) {
        for (OrderStatus s : allowed) {
            if (this.status == s) return;
        }
        throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
    }
}