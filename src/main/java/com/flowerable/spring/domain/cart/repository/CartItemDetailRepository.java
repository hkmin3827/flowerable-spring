package com.flowerable.spring.domain.cart.repository;

import com.flowerable.spring.domain.cart.entity.CartItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemDetailRepository extends JpaRepository<CartItemDetail, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM CartItemDetail cd
        WHERE cd.cartItem.id IN (
            SELECT ci.id FROM CartItem ci WHERE ci.cart.user.id = :userId
        )
    """)
    int deleteAllByCartUserId(@Param("userId") Long userId);
}
