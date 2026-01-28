package com.flowerable.spring.repository;

import com.flowerable.spring.constant.Provider;
import com.flowerable.spring.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    Optional<Account> findByEmailAndDeletedAtIsNull(String email);

    Optional<Account> findByProviderAndProviderIdAndDeletedAtIsNull(Provider provider, String providerId);
}
