package com.flowerable.spring.domain.order.repository;

import com.flowerable.spring.domain.order.entity.OrderNumberSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderNumberSequenceRepository
        extends JpaRepository<OrderNumberSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from OrderNumberSequence s where s.orderDate = :date")
    Optional<OrderNumberSequence> findByDateForUpdate(@Param("date") LocalDate date);
}