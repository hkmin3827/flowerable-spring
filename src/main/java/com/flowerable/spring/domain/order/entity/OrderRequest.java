package com.flowerable.spring.entity.order;

import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.entity.payment.Payment;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.entity.user.User;
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

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false)
    private Integer totalFlowerPrice;   // 각 orderItem calculateItemPrice의 합

    @Column(nullable = false)
    private Integer totalPrice;

    private String wrappingColorName;

    private Integer wrappingExtraPrice = 0;

    @Column(nullable = true, length = 100)
    private String message;  // 요청 사항

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime canceledAt;

    @OneToMany(mappedBy = "order")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "orderRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static OrderRequest create(
            String orderNumber,
            User user,
            Shop shop,
            String wrappingColorName,
            Integer wrappingExtraPrice,
            List<OrderItem> orderItems,
            String message) {
        OrderRequest order = new OrderRequest();
        order.orderNumber = orderNumber;
        order.user = user;
        order.shop = shop;
        order.status = OrderStatus.CREATED;
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

    public void markRequested() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("결제 전 상태가 아닙니다.");
        }
        this.status = OrderStatus.REQUESTED;
    }


    public void markCanceledAt() {
        this.canceledAt = LocalDateTime.now();
    }

    public void cancel() {
        validateStatus(OrderStatus.REQUESTED);
        this.status = OrderStatus.CANCELED;
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

    public boolean isPaid() {
        return this.status.isPaid();
    }
}