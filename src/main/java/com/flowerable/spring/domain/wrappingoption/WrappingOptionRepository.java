package com.flowerable.spring.domain.wrappingoption;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WrappingOptionRepository extends JpaRepository<WrappingOption, Long> {

    @EntityGraph(attributePaths = {"colorNames"})
    Optional<WrappingOption> findByShopId(Long shopId);
}
