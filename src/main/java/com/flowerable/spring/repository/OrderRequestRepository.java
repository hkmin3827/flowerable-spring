package com.flowerable.spring.repository;

import com.flowerable.spring.constant.order.OrderStatus;
import com.flowerable.spring.dto.order.OrderListRes;
import com.flowerable.spring.entity.order.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {


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
    """)
    Optional<OrderRequest> findDetailForStatusChange(@Param("orderId") Long orderId, @Param("shopId") Long shopId);

    @Query("""
    select new com.flowerable.spring.dto.order.OrderListRes(
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
    group by o.id, o.orderNumber, o.status, o.totalPrice, o.createdAt, s.shopName, u.name
    order by o.createdAt desc
    """)
    Page<OrderListRes> findShopOrders(
            @Param("shopId") Long shopId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    @Query("""
    select new com.flowerable.spring.dto.order.OrderListRes(
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
        join fetch sf.flower
        where s.id = :shopId
        and o.id = :orderId
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
        join fetch oi.shopFlower sf
        join fetch sf.flower
        where u.id = :userId
        and o.id = :orderId
    """)
    Optional<OrderRequest> findUserOrderDetails(
            @Param("userId") Long userId,
            @Param("orderId") Long orderId
    );

    /**
     * Shop 대시보드: REQUESTED 상태 최신 주문 조회
     */
    @Query("""
    select new com.flowerable.spring.dto.order.OrderListRes(
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


    /**
     * 관리자용 주문 조회 (동적 검색)
     * status, userId, shopId, from, to 모두 optional
     */
    @Query("""
    SELECT o FROM OrderRequest o
        JOIN o.shop s
        JOIN o.user u
        WHERE (:status IS NULL OR o.status = :status)
        AND (:userId IS NULL OR u.id = :userId)
        AND (:shopId IS NULL OR s.id = :shopId)
        AND (:from IS NULL OR o.createdAt >= :from)
        AND (:to IS NULL OR o.createdAt <= :to)
        ORDER BY o.createdAt DESC
""")
    Page<OrderRequest> findAdminOrders(
            @Param("status") OrderStatus status,
            @Param("userId") Long userId,
            @Param("shopId") Long shopId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    /**
     * 주문 상세 조회 (OrderItems fetch join)
     */
    @Query("""
        SELECT DISTINCT o FROM OrderRequest o
        join fetch o.shop s
        join fetch o.user u
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.shopFlower sf
        LEFT JOIN FETCH sf.flower
        WHERE o.id = :orderId
    """)
    OrderRequest findByIdWithItems(@Param("orderId") Long orderId);
}
