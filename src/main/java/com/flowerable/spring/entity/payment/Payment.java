package com.flowerable.spring.entity.payment;

import com.flowerable.spring.constant.order.OrderCancelReason;
import com.flowerable.spring.constant.payment.PaymentStatus;
import com.flowerable.spring.entity.order.OrderRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;      // Toss에서 발급

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderRequest order;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.READY; // READY, DONE, FAILED

    @Column(nullable = true)
    private String failReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void markDone(String paymentKey) {
        this.status = PaymentStatus.DONE;
        this.paymentKey = paymentKey;
    }

    public void markFailed(String message){
        this.status = PaymentStatus.FAILED;
        this.failReason = message;
    }

    public static Payment createReady(OrderRequest order, Integer amount){
        Payment payment = new Payment();
        payment.status = PaymentStatus.READY;
        payment.order = order;
        payment.amount = amount;
        return payment;
    }

    public void markCanceled(OrderCancelReason reason) {
        this.status = PaymentStatus.CANCELED;
        this.failReason = reason.getDescription();
    }
}
