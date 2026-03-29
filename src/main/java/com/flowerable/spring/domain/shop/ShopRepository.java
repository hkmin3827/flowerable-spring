package com.flowerable.spring.domain.shop;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import com.flowerable.spring.application.admin.dto.AdminShopListRes;
import com.flowerable.spring.application.shop.dto.ShopSearchRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("select s.id from Shop s where s.account.id = :accountId")
    Optional<Long> findIdByAccountId(Long accountId);

    @Query("select s from Shop s join fetch s.account a where s.id = :shopId")
    Optional<Shop> findById(@Param("shopId") Long shopId);

    @Query("""
            select s
            from Shop s
            where s.id = :shopId
                and s.status = com.flowerable.spring.domain.shop.constant.ShopStatus.ACTIVE
                and s.deletedAt is null
            """)
    Optional<Shop> findByIdAndDeletedAtIsNullAndIsActive(@Param("shopId") Long shopId);

    Optional<Shop> findByAccountIdAndDeletedAtIsNull(Long accountId);

    // 사용자 꽃 + 지역 선택 후
    @Query("""
    select new com.flowerable.spring.application.shop.dto.ShopSearchRes(
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
            and s.status = com.flowerable.spring.domain.shop.constant.ShopStatus.ACTIVE
            and s.deletedAt is null
    """)
    Optional<Shop> findDetailWithFlowers(@Param("shopId") Long shopId);

    // 관리자용 디테일 조회
    @Query("""
        select s
        from Shop s
        join fetch s.account
        where s.id = :id
    """)
    Optional<Shop> findDetailById(@Param("id") Long id);

    @Query("""
    select
            s.id as id,
            a.email as accountEmail,
            a.telnum as accountTelnum,
            a.status as accountStatus,
            s.shopName as shopName,
            s.region as region,
            s.district as district,
            s.address as address,
            s.status as status,
            s.registerAt as registerAt
        from Shop s
        join s.account a
        where (:shopStatus is null or s.status = :shopStatus)
          and (:accountStatus is null or a.status = :accountStatus)
        order by s.id desc
    """)
    Page<AdminShopListRes> findAdminShops(
            @Param("shopStatus") ShopStatus shopStatus,
            @Param("accountStatus") AccountStatus accountStatus,
            Pageable pageable
    );

    @Query("""
        select distinct s
        from Shop s
        left join fetch s.shopFlowers sf
        left join fetch sf.flower
        where s.account.id = :accountId
          and s.deletedAt is null
    """)
    Optional<Shop> findMyDetail(@Param("accountId") Long accountId);

    // 관리자용
    @Query("""
    select
        s.id as id,
        a.email as accountEmail,
        a.telnum as accountTelnum,
        a.status as accountStatus,
        s.shopName as shopName,
        s.region as region,
        s.district as district,
        s.address as address,
        s.status as status,
        s.registerAt as registerAt
    from Shop s
    join s.account a
    where (:keyword is null 
           or lower(s.shopName) like lower(concat('%', :keyword, '%')))
    order by s.id desc
""")
    Page<AdminShopListRes> searchAdminShops(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
