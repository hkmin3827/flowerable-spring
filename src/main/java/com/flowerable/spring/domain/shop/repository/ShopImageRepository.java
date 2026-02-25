package com.flowerable.spring.repository;

import com.flowerable.spring.entity.shop.ShopImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopImageRepository extends JpaRepository<ShopImage, Long> {

    List<ShopImage> findTop8ByShopIdAndIdLessThanOrderByIdDesc(
            Long shopId,
            Long lastId
    );

    List<ShopImage> findTop8ByShopIdOrderByIdDesc(Long shopId);

    // 최신 5개 (샵 상세)
    List<ShopImage> findTop5ByShopIdOrderByCreatedAtDesc(Long shopId);

    // 대표 이미지 1개 (샵 목록)
    Optional<ShopImage> findByShopIdAndIsThumbnailTrue(Long shopId);


    // 단건 조회 (삭제용)
    Optional<ShopImage> findByIdAndShopId(Long id, Long shopId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ShopImage si
        set si.isThumbnail = false
        where si.shop.id = :shopId
    """)
    void clearAllThumbnails(@Param("shopId") Long shopId);
}
