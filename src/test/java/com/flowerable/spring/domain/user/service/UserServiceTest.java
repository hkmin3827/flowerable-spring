package com.flowerable.spring.domain.user.service;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.auth.constant.Provider;
import com.flowerable.spring.domain.auth.entity.Account;
import com.flowerable.spring.domain.user.dto.UserDetailRes;
import com.flowerable.spring.domain.user.dto.UserUpdateInfoReq;
import com.flowerable.spring.domain.user.entity.User;
import com.flowerable.spring.domain.user.repository.UserRepository;
import com.flowerable.spring.global.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private Account account;
    private User user;

    @BeforeEach
    void setUp() {
        account = Account.createUserAccount("test@example.com", "encodedPassword", "01012345678");
        user = User.create(account, "테스트유저");
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyDetails_existingAccount_returnsUserDetailRes() {
        Long accountId = 1L;
        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId))
                .willReturn(Optional.of(user));

        UserDetailRes result = userService.getMyDetails(accountId);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("테스트유저");
        assertThat(result.getTelnum()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(result.getProvider()).isEqualTo(Provider.LOCAL);

        verify(userRepository).findByAccountIdAndDeletedAtIsNull(accountId);
    }

    @Test
    @DisplayName("내 정보 조회 - 존재하지 않는 계정이면 UserNotFoundException 발생")
    void getMyDetails_nonExistingAccount_throwsUserNotFoundException() {
        Long accountId = 999L;
        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyDetails(accountId))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("특정 유저 조회 성공")
    void getUserDetails_existingUser_returnsUserDetailRes() {
        Long userId = 1L;
        given(userRepository.findByIdAndDeletedAtIsNull(userId))
                .willReturn(Optional.of(user));

        UserDetailRes result = userService.getUserDetails(userId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트유저");
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findByIdAndDeletedAtIsNull(userId);
    }

    @Test
    @DisplayName("특정 유저 조회 - 존재하지 않으면 UserNotFoundException 발생")
    void getUserDetails_nonExistingUser_throwsUserNotFoundException() {
        Long userId = 999L;
        given(userRepository.findByIdAndDeletedAtIsNull(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserDetails(userId))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("유저 정보 업데이트 성공 - 이름 변경")
    void updateUserInfo_withName_updatesName() {
        Long accountId = 1L;
        UserUpdateInfoReq req = UserUpdateInfoReq.builder()
                .name("변경이름")
                .build();

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId))
                .willReturn(Optional.of(user));

        userService.updateUserInfo(accountId, req);

        assertThat(user.getName()).isEqualTo("변경이름");
        verify(userRepository).findByAccountIdAndDeletedAtIsNull(accountId);
    }

    @Test
    @DisplayName("유저 정보 업데이트 성공 - 전화번호 변경")
    void updateUserInfo_withTelnum_updatesTelnum() {
        Long accountId = 1L;
        UserUpdateInfoReq req = UserUpdateInfoReq.builder()
                .telnum("01099998888")
                .build();

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId))
                .willReturn(Optional.of(user));

        userService.updateUserInfo(accountId, req);

        assertThat(account.getTelnum()).isEqualTo("01099998888");
    }

    @Test
    @DisplayName("유저 정보 업데이트 - 존재하지 않는 계정이면 UserNotFoundException 발생")
    void updateUserInfo_nonExistingAccount_throwsUserNotFoundException() {
        Long accountId = 999L;
        UserUpdateInfoReq req = UserUpdateInfoReq.builder()
                .name("변경이름")
                .build();

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserInfo(accountId, req))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("유저 정보 업데이트 - null 필드는 변경하지 않음")
    void updateUserInfo_withNullFields_doesNotUpdateNullFields() {
        Long accountId = 1L;
        UserUpdateInfoReq req = UserUpdateInfoReq.builder()
                .name(null)
                .telnum(null)
                .build();

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId))
                .willReturn(Optional.of(user));

        userService.updateUserInfo(accountId, req);

        assertThat(user.getName()).isEqualTo("테스트유저");
        assertThat(account.getTelnum()).isEqualTo("01012345678");
    }
}
