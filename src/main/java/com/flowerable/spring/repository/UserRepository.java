package com.flowerable.spring.repository;

import com.flowerable.spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccountIdAndDeletedAtIsNull(Long accountId);

    Optional<User> findByAccountId(Long accountId);
}
