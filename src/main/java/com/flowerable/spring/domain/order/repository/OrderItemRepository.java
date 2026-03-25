package com.flowerable.spring.domain.order.repository;

import com.flowerable.spring.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM OrderItem oi WHERE oi.orderRequest.id IN :orderIds")
    int deleteAllByOrderRequestIdIn(@Param("orderIds") List<Long> orderIds);
}
