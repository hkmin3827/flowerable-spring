package com.flowerable.spring.domain.order.repository;

import com.flowerable.spring.domain.order.entity.OrderCancelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderCancelLogRepository extends JpaRepository<OrderCancelLog, Long> {
    Optional<OrderCancelLog> findByOrderRequestId(Long orderRequestId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM OrderCancelLog l WHERE l.orderRequestId IN :orderIds")
    int deleteAllByOrderRequestIds(@Param("orderIds") List<Long> orderIds);
}