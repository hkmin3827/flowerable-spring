package com.flowerable.spring.domain.order.repository;

import com.flowerable.spring.domain.order.entity.OrderCancelLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderCancelLogRepository extends JpaRepository<OrderCancelLog, Long> {
    Optional<OrderCancelLog> findByOrderRequestId(Long orderRequestId);
}