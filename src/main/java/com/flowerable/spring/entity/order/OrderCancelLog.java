package com.flowerable.spring.entity.order;

import com.flowerable.spring.constant.order.OrderCancelBy;
import com.flowerable.spring.constant.order.OrderCancelReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_cancel_logs")
@Getter
@NoArgsConstructor
public class OrderCancelLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderCancelBy canceledBy; // USER or SHOP

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private OrderCancelReason cancelReason;

    private OrderCancelLog(Long orderRequestId, OrderCancelBy canceledBy, OrderCancelReason cancelReason) {
        this.orderRequestId = orderRequestId;
        this.canceledBy = canceledBy;
        this.cancelReason = cancelReason;
    }

    public static OrderCancelLog create(Long orderRequestId, OrderCancelBy canceledBy, OrderCancelReason cancelReason) {
        return new OrderCancelLog(orderRequestId, canceledBy, cancelReason);
    }
}