package com.flowerable.spring.domain.payment.repository;

import com.flowerable.spring.domain.payment.constant.PaymentStatus;
import com.flowerable.spring.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrderIdAndStatusOrderByCreatedAtDesc(
            Long orderId,
            PaymentStatus status
    );
}
