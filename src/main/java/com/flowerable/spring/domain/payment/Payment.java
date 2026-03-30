package com.flowerable.spring.domain.payment;

import com.flowerable.spring.domain.order.constant.OrderCancelReason;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments",
        indexes = {
                @Index(name = "idx_payment_order_id_status", columnList = "order_id, status"),
                @Index(name = "idx_payment_payment_key", columnList = "payment_key", unique = true)
        }
)
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderRequest order;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.READY;

    @Column(nullable = true)
    private String failReason;

    @Column
    private String cancelReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    public void markDone() {
        this.status = PaymentStatus.DONE;
        this.approvedAt = LocalDateTime.now();
    }

    public void markFailed(String message){
        this.status = PaymentStatus.FAILED;
        this.failReason = message;
    }

    public static Payment createReady(OrderRequest order, Integer amount, String paymentKey) {
        Payment payment = new Payment();
        payment.status = PaymentStatus.READY;
        payment.order = order;
        payment.amount = amount;
        payment.paymentKey = paymentKey;
        return payment;
    }

    public void markCanceled(OrderCancelReason cancelReason) {
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = cancelReason.getDescription();
    }
}
