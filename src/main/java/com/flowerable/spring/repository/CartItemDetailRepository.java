package com.flowerable.spring.repository;

import com.flowerable.spring.constant.shopflower.Color;
import com.flowerable.spring.entity.cart.CartItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemDetailRepository extends JpaRepository<CartItemDetail, Long> {
    List<CartItemDetail> findByCartItemId(Long cartItemId);
    Optional<CartItemDetail> findByCartItemIdAndShopFlowerIdAndFlowerColor(
        Long cartItemId, Long shopFlowerId, Color flowerColor
    );
}
