package com.flowerable.spring.repository;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.entity.account.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);

    Optional<Account> findByProviderAndProviderIdAndDeletedAtIsNull(Provider provider, String providerId);

    boolean existsByEmailAndDeletedAtIsNull(String email);
    boolean existsByTelnumAndDeletedAtIsNull(String telnum);

}
