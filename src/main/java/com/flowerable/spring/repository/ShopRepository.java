package com.flowerable.spring.repository;

import com.flowerable.spring.constant.region.District;
import com.flowerable.spring.constant.region.Region;
import com.flowerable.spring.constant.shop.ShopStatus;
import com.flowerable.spring.dto.admin.AdminShopListRes;
import com.flowerable.spring.dto.shop.ShopSearchRes;
import com.flowerable.spring.entity.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("select s.id from Shop s where s.account.id = :accountId")
    Optional<Long> findIdByAccountId(Long accountId);


    @Query("""
            select s
            from Shop s
            where s.id = :shopId
                and s.status = 'ACTIVE'
                and s.deletedAt is null
            """)
    Optional<Shop> findByIdAndDeletedAtIsNullAndIsActive(@Param("shopId") Long shopId);

    Optional<Shop> findByAccountIdAndDeletedAtIsNull(Long accountId);
    // 사용자 꽃 + 지역 선택 후
    @Query("""
    select new com.flowerable.spring.dto.shop.ShopSearchRes(
        s.id,
        s.shopName,
        a.telnum,
        s.description,
        s.address,
        s.region,
        s.district
    )
    from Shop s
    join s.account a
    join s.shopFlowers sf
    where sf.flower.name = :flowerName
      and s.status = 'ACTIVE'
      and (:region IS NULL OR s.region = :region)
      and (:district IS NULL OR s.district = :district)
""")
    Page<ShopSearchRes> findShopsByFilter(
            @Param("flowerName") String flowerName,
            @Param("region") Region region,
            @Param("district") District district,
            Pageable pageable
    );

    // 사용자용 디테일 조회
    @Query("""
    select distinct s
    from Shop s
    left join fetch s.shopFlowers sf
    left join fetch sf.flower
    where s.id = :shopId
            and s.status = 'ACTIVE'
            and s.deletedAt is null
    """)
    Optional<Shop> findDetailWithFlowers(@Param("shopId") Long shopId);

    // 관리자용 디테일 조회
    @Query("""
        select s
        from Shop s
        join fetch s.account
        where s.id = :id
          and s.deletedAt is null
    """)
    Optional<Shop> findDetailById(@Param("id") Long id);

    // 탈퇴 게정은 조회 제외, 상태관리용
    @Query("""
    select
            s.id as id,
            a.email as accountEmail,
            s.shopName as shopName,
            s.region as region,
            s.district as district,
            s.address as address,
            s.status as status
        from Shop s
        join s.account a
        where s.status = :status
            and s.deletedAt is null
    """)
    Page<AdminShopListRes> findAdminShopsByStatus(@Param("status") ShopStatus status, Pageable pageable);

    @Query("""
        select distinct s
        from Shop s
        left join fetch s.shopFlowers sf
        left join fetch sf.flower
        where s.account.id = :accountId
          and s.deletedAt is null
    """)
    Optional<Shop> findMyDetail(@Param("accountId") Long accountId);

    @Query("""
    select
            s.id as id,
            a.email as accountEmail,
            s.shopName as shopName,
            s.region as region,
            s.district as district,
            s.address as address,
            s.status as status
        from Shop s
        join s.account a
        where s.deletedAt is null
        ORDER BY s.id DESC
    """)
    Page<AdminShopListRes> findAllAdminShops(Pageable pageable);

    /**
     * 관리자용 샵 검색 (샵명)
     */
    @Query("""
    SELECT s
        FROM Shop s
        JOIN FETCH s.account
        WHERE (:keyword IS NULL OR LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY s.id DESC
    """)
    Page<AdminShopListRes> searchAdminShops(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
