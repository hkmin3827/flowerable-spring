package com.flowerable.spring.repository;

import com.flowerable.spring.constant.flower.Season;
import com.flowerable.spring.dto.admin.AdminFlowerListRes;
import com.flowerable.spring.entity.flower.Flower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Long> {
    Optional<Flower> findByIdAndActiveTrue(Long id);

    boolean existsByName(String name);
    Page<Flower> findAll(Pageable pageable);
    Page<Flower> findAllByActiveTrue(Pageable pageable);

    // 사용자용 조회 (category 기준, active = true)
    @Query("""
    select f
    from Flower f
    where f.active = true
      and (:category is null or f.category = :category)
    """)
    Page<Flower> findUserFlowersByCategory(
            @Param("category") Season category,
            Pageable pageable
    );

    // 관리자용 조회 (active 기준)
    @Query("""
        select f
        from Flower f
        where (:active is null or f.active = :active)
    """)
    Page<AdminFlowerListRes> findByActiveCondition(
            @Param("active") Boolean active,
            Pageable pageable
    );
}
