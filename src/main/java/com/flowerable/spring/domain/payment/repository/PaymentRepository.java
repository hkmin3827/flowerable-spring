package com.flowerable.spring.repository;

import com.flowerable.spring.constant.payment.PaymentStatus;
import com.flowerable.spring.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrderIdAndStatusOrderByCreatedAtDesc(
            Long orderId,
            PaymentStatus status
    );
}
