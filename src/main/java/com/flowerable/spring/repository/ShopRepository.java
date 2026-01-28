package com.flowerable.spring.repository;

import com.flowerable.spring.entity.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByEmail(String email);
}
