package com.flowerable.spring.repository;

import com.flowerable.spring.dto.shopflower.ShopFlowerRes;
import com.flowerable.spring.entity.shopflower.ShopFlower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface ShopFlowerRepository extends JpaRepository<ShopFlower, Long> {

    @Query("""
        select sf
        from ShopFlower sf
        join fetch sf.flower
        where sf.shop.id = :shopId
            and sf.id = :id
    """)
    Optional<ShopFlower> findByIdAndShopId(@Param("id") Long id, @Param("shopId") Long shopId);

    Optional<ShopFlower> findByIdAndShopIdAndOnSaleTrue(Long id, Long shopId);

    @Query("""
    select sf
    from ShopFlower sf
    join fetch sf.shop s
    join fetch s.account
    where sf.id = :id
    """)
    Optional<ShopFlower> findWithShopAndAccount(@Param("id") Long id);

    // ✅ 중복 등록 방지용 (exists 쿼리)
    @Query("""
        select count(sf) > 0
        from ShopFlower sf
        where sf.shop.id = :shopId
          and sf.flower.id = :flowerId
    """)
    boolean existsByShopIdAndFlowerId(
            @Param("shopId") Long shopId,
            @Param("flowerId") Long flowerId
    );

    // 보유한 샵플라워 조회 (onSale 여부 필터링, onSale = null 은 전체 조회)
    @Query("""
        select sf
        from ShopFlower sf
        join sf.shop s
        join fetch sf.flower
        left join fetch sf.colors
        where s.id = :shopId
          and (:onSale is null or sf.onSale = :onSale)
    """)
    Page<ShopFlower> findMyShopFlowers(
            @Param("shopId") Long shopId,
            @Param("onSale") Boolean onSale,
            Pageable pageable
    );

    // Flower 비활성/삭제 시 일괄 판매 숨김 처리용
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ShopFlower sf
        set sf.onSale = false
        where sf.flower.id = :flowerId
          and sf.onSale = true
    """)
    void stopSaleByFlowerId(@Param("flowerId") Long flowerId);

}
