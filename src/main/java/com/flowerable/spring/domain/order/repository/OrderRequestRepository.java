package com.flowerable.spring.domain.order.repository;

import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.domain.order.dto.OrderListRes;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long>, JpaSpecificationExecutor<OrderRequest> {

    @Query("""
            select o
            from OrderRequest o
            join fetch o.shop s
            where o.orderNumber = :orderNumber
            """)
    Optional<OrderRequest> findByOrderNumber(@Param("orderNumber") String orderNumber);

    // 사용자 주문 취소용
    @Query("""
            select o
            from OrderRequest o
            join fetch o.shop s
            join fetch o.user u
            where o.id = :orderId
                and o.canceledAt is null
                and u.id = :userId
            """)
    Optional<OrderRequest> findByIdAndUserId(@Param("orderId")Long id, @Param("userId") Long userId);

    // 샵에서 주문 상태관리용
    @Query("""
        select o
        from OrderRequest o
        join fetch o.user u
        join fetch o.shop s
        join fetch o.orderItems oi
        where o.id = :orderId
          and o.canceledAt is null
          and s.id = :shopId
          and o.status <> 'CREATED'
    """)
    Optional<OrderRequest> findDetailForStatusChange(@Param("orderId") Long orderId, @Param("shopId") Long shopId);

    @Query("""
    select new com.flowerable.spring.domain.order.dto.OrderListRes(
        o.id,
        o.orderNumber,
        o.status,
        o.totalPrice,
        o.createdAt,
        s.shopName,
        u.name,
        coalesce(sum(oi.quantity), 0)
    )
    from OrderRequest o
        left join o.orderItems oi
        join o.shop s
        join o.user u
    where s.id = :shopId
      and (:status is null or o.status = :status)
      and o.status <> 'CREATED'
    group by o.id, o.orderNumber, o.status, o.totalPrice, o.createdAt, s.shopName, u.name
    order by o.createdAt desc
    """)
    Page<OrderListRes> findShopOrders(
            @Param("shopId") Long shopId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    @Query("""
    select new com.flowerable.spring.domain.order.dto.OrderListRes(
        o.id,
        o.orderNumber,
        o.status,
        o.totalPrice,
        o.createdAt,
        s.shopName,
        u.name,
        coalesce(sum(oi.quantity), 0)
    )
    from OrderRequest o
    left join o.orderItems oi
    join o.shop s
    join o.user u
    where u.id = :userId
    group by o.id, o.orderNumber, o.status, o.totalPrice, o.createdAt, s.shopName, u.name
    order by o.createdAt desc
    """)
    Page<OrderListRes> findUserOrders(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
        select o
        from OrderRequest o
        join fetch o.orderItems oi
        join fetch oi.shopFlower sf
        join fetch o.shop s
        join fetch o.user u
        join fetch u.account a
        join fetch sf.flower
        where s.id = :shopId
        and o.id = :orderId
        and o.status <> 'CREATED'
    """)
    Optional<OrderRequest> findShopOrderDetails(
            @Param("shopId") Long shopId,
            @Param("orderId") Long orderId
    );

    @Query("""
        select o
        from OrderRequest o
        join fetch o.orderItems oi
        join fetch o.user u
        join fetch o.shop s
        join fetch s.account a
        join fetch oi.shopFlower sf
        join fetch sf.flower
        where u.id = :userId
        and o.id = :orderId
    """)
    Optional<OrderRequest> findUserOrderDetails(
            @Param("userId") Long userId,
            @Param("orderId") Long orderId
    );

    @Query("""
    select new com.flowerable.spring.domain.order.dto.OrderListRes(
        o.id,
        o.orderNumber,
        o.status,
        o.totalPrice,
        o.createdAt,
        s.shopName,
        u.name,
        coalesce(sum(oi.quantity), 0)
    )
    from OrderRequest o
    left join o.orderItems oi
    join o.shop s
    join o.user u
    where s.id = :shopId
      and o.status = 'REQUESTED'
    group by o.id, o.orderNumber, o.status, o.totalPrice, o.createdAt, s.shopName, u.name
    order by o.createdAt desc
""")
    Page<OrderListRes> findRecentRequestedOrders(
            @Param("shopId") Long shopId,
            Pageable pageable
    );


    @Query("""
        SELECT DISTINCT o FROM OrderRequest o
        JOIN FETCH o.shop s
        JOIN FETCH s.account sa
        JOIN FETCH o.user u
        JOIN FETCH u.account ua
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.shopFlower sf
        LEFT JOIN FETCH sf.flower
        WHERE o.id = :orderId
    """)
    Optional<OrderRequest> findByIdWithItems(@Param("orderId") Long orderId);

    @Override
    @EntityGraph(attributePaths = {"shop", "user"}) // 여기서 연관 관계를 미리 정의합니다.
    Page<OrderRequest> findAll(Specification<OrderRequest> spec, Pageable pageable);
}
