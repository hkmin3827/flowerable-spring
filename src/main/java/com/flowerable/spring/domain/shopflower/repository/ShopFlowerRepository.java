package com.flowerable.spring.domain.shopflower.repository;

import com.flowerable.spring.domain.shopflower.dto.ShopFlowerOrderCountDto;
import com.flowerable.spring.domain.shopflower.entity.ShopFlower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ShopFlower sf
        set sf.onSale = false
        where sf.flower.id = :flowerId
          and sf.onSale = true
    """)
    void stopSaleByFlowerId(@Param("flowerId") Long flowerId);

    @Query("""
    SELECT new com.flowerable.spring.domain.shopflower.dto.ShopFlowerOrderCountDto(
            f.name,
            COUNT(DISTINCT o.id)
        )
        FROM OrderItem oi
        JOIN oi.orderRequest o
        JOIN oi.shopFlower sf
        JOIN sf.flower f
        WHERE o.shop.id = :shopId
        GROUP BY f.id, f.name
        ORDER BY COUNT(DISTINCT o.id) DESC
    """)
    List<ShopFlowerOrderCountDto> findTop5FlowersByOrderCount(@Param("shopId") Long shopId, Pageable pageable);
}
