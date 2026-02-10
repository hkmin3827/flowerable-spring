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
    Optional<OrderRequest> findByIdAndUserId(Long id, Long userId);

    // 샵에서 주문 상태관리용
    @Query("""
        select o
        from OrderRequest o
        join fetch o.orderItems oi
        where o.id = :orderId
          and o.canceledAt is null
          and o.shopId = :shopId
    """)
    Optional<OrderRequest> findDetailForStatusChange(@Param("orderId") Long orderId, @Param("shopId") Long shopId);

    @Query("""
    select new com.flowerable.spring.dto.order.OrderListRes(
        o.id,
        o.orderNumber,
        o.status,
        o.totalPrice,
        o.createdAt,
        coalesce(sum(oi.quantity), 0)
    )
    from OrderRequest o
    left join o.orderItems oi
    where o.shopId = :shopId
      and (:status is null or o.status = :status)
    group by o.id, o.orderNumber, o.status, o.totalPrice, o.createdAt  
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
        coalesce(sum(oi.quantity), 0)
    )
    from OrderRequest o
    left join o.orderItems oi
    where o.userId = :userId
    group by o.id, o.orderNumber, o.status, o.totalPrice, o.createdAt
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
        join fetch sf.flower
        where o.shopId = :shopId
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
        join fetch oi.shopFlower sf
        join fetch sf.flower
        where o.userId = :userId
        and o.id = :orderId
    """)
    Optional<OrderRequest> findUserOrderDetails(
            @Param("userId") Long userId,
            @Param("orderId") Long orderId
    );


    /**
     * 관리자 리스트 조회(요약)
     * - 페이징 때문에 orderItems fetch join 금지
     */
    @Query("""
        select o
        from OrderRequest o
        where (:status is null or o.status = :status)
          and (:userId is null or o.userId = :userId)
          and (:shopId is null or o.shopId = :shopId)
          and (:from is null or o.createdAt >= cast(:from as timestamp))
          and (:to   is null or o.createdAt <= cast(:to as timestamp))
        order by o.createdAt desc
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
     * 관리자 상세 조회(아이템 + 꽃 정보까지)
     * - 스냅샷 가격은 OrderItem.basePrice를 쓰고
     * - 꽃 이름은 표시용으로만 join해서 가져옴
     */
    @Query("""
        select distinct o
        from OrderRequest o
        left join fetch o.orderItems oi
        left join fetch oi.shopFlower sf
        left join fetch sf.flower f
        where o.id = :orderId
    """)
    Optional<OrderRequest> findAdminOrderDetail(@Param("orderId") Long orderId);
}
