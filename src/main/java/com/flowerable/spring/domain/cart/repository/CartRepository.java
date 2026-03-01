package com.flowerable.spring.domain.cart.repository;

import com.flowerable.spring.domain.cart.entity.Cart;
import com.flowerable.spring.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserId(Long userId);
}
