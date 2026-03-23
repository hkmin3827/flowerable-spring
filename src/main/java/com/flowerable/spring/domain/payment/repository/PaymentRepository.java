package com.flowerable.spring.domain.payment.repository;

import com.flowerable.spring.domain.payment.constant.PaymentStatus;
import com.flowerable.spring.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrderIdAndStatusOrderByCreatedAtDesc(
            Long orderId,
            PaymentStatus status
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Payment p WHERE p.order.id IN :orderIds")
    int deleteAllByOrderIds(@Param("orderIds") List<Long> orderIds);
}
