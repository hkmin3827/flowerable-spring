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
            a.telnum as accountTelnum,
            a.status as accountStatus,
            u.name as name,
            u.active as active,
            u.createdAt as createdAt
        from User u
        join u.account a
        where a.status = :status
    """)
    Page<AdminUserListRes> findAdminUsersByStatus(@Param("status")AccountStatus status, Pageable pageable);

    @Query("""
    select
            u.id as id,
            a.email as accountEmail,
            a.telnum as accountTelnum,
            a.status as accountStatus,
            u.name as name,
            u.active as active,
            u.createdAt as createdAt
        from User u
        join u.account a
        where a.status in :statuses
        and a.role = com.flowerable.spring.constant.auth.Role.ROLE_USER
    """)
    Page<AdminUserListRes> findAdminUsersByAccountStatuses(
            @Param("statuses") List<AccountStatus> statuses,
            Pageable pageable
    );

    @Query("""
        SELECT 
            u.id as id,
            a.email as accountEmail,
            a.telnum as accountTelnum,
            a.status as accountStatus,
            u.name as name,
            u.active as active,
            u.createdAt as createdAt
        FROM User u 
        JOIN u.account a
        WHERE (:keyword IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY u.id DESC
    """)
    Page<AdminUserListRes> searchAdminUsers(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        select u
        from User u
        join fetch u.account
        where u.id = :id
    """)
    Optional<User> findDetailById(@Param("id") Long id);


    @Query("""
        select u
        from User u
        join fetch u.account
        where u.id = :userId
    """)
    Optional<User> findAdminUserById(@Param("userId") Long userId);

    Optional<User> findByAccountIdAndDeletedAtIsNull(Long accountId);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Optional<User> findByAccountId(Long accountId);
}
