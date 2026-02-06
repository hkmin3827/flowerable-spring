//package com.flowerable.spring.repository;
//
//import com.flowerable.spring.constant.order.OrderStatus;
//import com.flowerable.spring.entity.order.OrderRequest;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static com.flowerable.spring.entity.order.QOrderRequest.orderRequest;
//
//@Repository
//@RequiredArgsConstructor
//public class OrderRequestQueryRepository {
//
//    private final JPAQueryFactory queryFactory;
//
//    public Page<OrderRequest> findAdminOrders(
//            OrderStatus status,
//            Long userId,
//            Long shopId,
//            LocalDateTime from,
//            LocalDateTime to,
//            Pageable pageable
//    ) {
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (status != null) {
//            builder.and(orderRequest.status.eq(status));
//        }
//        if (userId != null) {
//            builder.and(orderRequest.userId.eq(userId));
//        }
//        if (shopId != null) {
//            builder.and(orderRequest.shopId.eq(shopId));
//        }
//        if (from != null) {
//            builder.and(orderRequest.createdAt.goe(from));
//        }
//        if (to != null) {
//            builder.and(orderRequest.createdAt.loe(to));
//        }
//
//        List<OrderRequest> content = queryFactory
//                .selectFrom(orderRequest)
//                .where(builder)
//                .orderBy(orderRequest.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        Long total = queryFactory
//                .select(orderRequest.count())
//                .from(orderRequest)
//                .where(builder)
//                .fetchOne();
//
//        return new PageImpl<>(
//                content,
//                pageable,
//                total != null ? total : 0
//        );
//    }
//}