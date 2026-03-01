package com.flowerable.spring.domain.wrappingoption.repository;

import com.flowerable.spring.domain.wrappingoption.entity.WrappingOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WrappingOptionRepository extends JpaRepository<WrappingOption, Long> {
    Optional<WrappingOption> findByShopId(Long shopId);
}
