package com.flowerable.spring.repository;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.dto.admin.AdminUserListRes;
import com.flowerable.spring.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u.id from User u where u.account.id = :accountId")
    Optional<Long> findIdByAccountId(Long accountId);

    @Query("""
    select
            u.id as id,
            a.email as accountEmail,
            a.status as accountStatus,
            u.name as name,
            u.active as active
        from User u
        join u.account a
        where a.status = :status
    """)
    Page<AdminUserListRes> findAdminUsersByStatus(@Param("status")AccountStatus status, Pageable pageable);

    @Query("""
    select
            u.id as id,
            a.email as accountEmail,
            a.status as accountStatus,
            u.name as name,
            u.active as active
        from User u
        join u.account a
        where a.status in :statuses
    """)
    Page<AdminUserListRes> findAdminUsersByAccountStatuses(
            @Param("statuses") List<AccountStatus> statuses,
            Pageable pageable
    );

    // 관리자용
    @Query("""
        select u
        from User u
        join fetch u.account
        where u.id = :id
          and u.deletedAt is null
    """)
    Optional<User> findDetailById(@Param("id") Long id);

    // 주문 생성용 조회
    @Query("""
    select count(u) > 0
    from User u
    join u.account a
    where u.id = :userId
      and u.active = true
      and a.status = 'ACTIVE'
    """)
    boolean existsActiveUser(@Param("userId") Long userId);

    @Query("""
        select u
        from User u
        join fetch u.account
        where u.id = :userId
          and u.deletedAt is null
    """)
    Optional<User> findAdminUserById(@Param("userId") Long userId);

    Optional<User> findByAccountIdAndDeletedAtIsNull(Long accountId);

    Optional<User> findByAccountId(Long accountId);
}
