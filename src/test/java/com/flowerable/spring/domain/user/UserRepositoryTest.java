package com.flowerable.spring.domain.user;

import com.flowerable.spring.application.admin.dto.AdminUserListRes;
import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.auth.Account;
import com.flowerable.spring.domain.auth.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties ={
        "spring.cloud.aws.parameterstore.enabled=false",
        "spring.config.import="
})
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        Account acc1 = accountRepository.save(Account.createUserAccount("user1@test.com", "pw", "0101"));
        userRepository.save(User.create(acc1, "테스터1"));
        acc1.suspend();

        Account acc2 = accountRepository.save(Account.createUserAccount("user2@test.com", "pw", "0102"));
        userRepository.save(User.create(acc2, "테스터2"));
    }

    @Test
    @DisplayName("관리자 유저 검색 - 키워드 포함 시 필터링 확인")
    void searchAdminUsers_withKeyword_returnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "스터1";

        Page<AdminUserListRes> result = userRepository.searchAdminUsers(keyword, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스터1");
    }

    @Test
    @DisplayName("관리자 유저 검색 - 키워드 NULL 시 전체 조회 확인")
    void searchAdminUsers_withNullKeyword_returnsAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<AdminUserListRes> result = userRepository.searchAdminUsers(null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }


    @Test
    @DisplayName("관리자 유저 검색 - AccountStatus 검색 시 필터링 확인")
    void searchAdminUsers_withStatus_returnsFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);

        List<AccountStatus> statuses = new ArrayList<>();
        statuses.add(AccountStatus.SUSPENDED);
        Page<AdminUserListRes> result = userRepository.findAdminUsersByAccountStatuses(statuses, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스터1");
    }
}
