package com.flowerable.spring.repository;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.entity.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    Optional<Account> findByEmailAndDeletedAtIsNull(String email);

    Optional<Account> findByProviderAndProviderIdAndDeletedAtIsNull(Provider provider, String providerId);

    boolean existsByEmailAndDeletedAtIsNull(String email);
    boolean existsByTelnumAndDeletedAtIsNull(String telnum);

//    // 관리자 기능 추가
//    Long countByRole(Role role);
//    Page<Account> findByAccountStatus(AccountStatus status, Pageable pageable);
//    Page<Account> findByRole(Role role, Pageable pageable);
//
//    @Query("SELECT a FROM Account a WHERE a.accountStatus = :status AND " +
//            "(LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(a.telnum) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<Account> findByAccountStatusAndSearchKeyword(
//            @Param("status") AccountStatus status,
//            @Param("keyword") String keyword,
//            Pageable pageable
//    );

}
