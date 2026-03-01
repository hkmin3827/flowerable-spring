package com.flowerable.spring.domain.cart.repository;

import com.flowerable.spring.domain.cart.entity.CartItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemDetailRepository extends JpaRepository<CartItemDetail, Long> {
}
