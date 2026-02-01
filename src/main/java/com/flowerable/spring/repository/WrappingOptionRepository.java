package com.flowerable.spring.repository;

import com.flowerable.spring.entity.wrappingoption.WrappingOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WrappingOptionRepository extends JpaRepository<WrappingOption, Long> {
    Optional<WrappingOption> findByShopId(Long shopId);
}
