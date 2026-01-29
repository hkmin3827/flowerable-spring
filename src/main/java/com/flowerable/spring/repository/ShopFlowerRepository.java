package com.flowerable.spring.repository;

import com.flowerable.spring.entity.shopflower.ShopFlower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopFlowerRepository extends JpaRepository<ShopFlower, Long> {
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

    // Flower 비활성/삭제 시 일괄 삭제용
    @Modifying
    @Query("""
        delete from ShopFlower sf
        where sf.flower.id = :flowerId
    """)
    void deleteByFlowerId(@Param("flowerId") Long flowerId);
}
