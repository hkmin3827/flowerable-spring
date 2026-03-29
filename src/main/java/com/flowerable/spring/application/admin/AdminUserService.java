package com.flowerable.spring.application.admin;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.application.admin.dto.AdminUserListRes;
import com.flowerable.spring.application.user.dto.UserDetailRes;
import com.flowerable.spring.domain.user.User;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import com.flowerable.spring.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<AdminUserListRes> getUsersByStatus(AccountStatus targetStatus, Pageable pageable) {
        if (targetStatus == null) {
            return userRepository.findAdminUsersByAccountStatuses(
                    List.of(AccountStatus.ACTIVE, AccountStatus.SUSPENDED),
                    pageable
            );
        }
        if (targetStatus == AccountStatus.DELETED) {
            return Page.empty(pageable);
        }
        return userRepository.findAdminUsersByStatus(targetStatus, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AdminUserListRes> searchUsers(String keyword, Pageable pageable) {
        String normalizedKeyword =
                (keyword == null || keyword.isBlank())
                        ? null
                        : keyword.trim();

        return userRepository.searchAdminUsers(keyword, pageable);
    }

    @Transactional
    public void changeStatus(Long userId, AccountStatus targetStatus) {
        User user = userRepository.findAdminUserById(userId)
                .orElseThrow(UserNotFoundException::new);
        
        // Account status (로그인 진입) 제한.
        // user.activate 메서드 추가 시 기능 제한가능, 현재 사용 x (확장용)
        switch (targetStatus){
            case ACTIVE -> user.getAccount().activate();
            case SUSPENDED -> user.getAccount().suspend();
            default -> throw new CustomException(ErrorCode.INVALID_ACCOUNT_STATUS);
        }
    }

    @Transactional(readOnly = true)
    public UserDetailRes getUserDetails(Long userId) {
        User user = userRepository
                .findDetailById(userId)
                .orElseThrow(UserNotFoundException::new);

        return UserDetailRes.builder()
                .id(user.getId())
                .email(user.getAccount().getEmail())
                .createdAt(user.getCreatedAt())
                .name(user.getName())
                .deletedAt(user.getDeletedAt())
                .telnum(user.getAccount().getTelnum())
                .active(user.isActive())
                .provider(user.getAccount().getProvider())
                .providerId(user.getAccount().getProviderId())
                .accountStatus(user.getAccount().getStatus())
                .build();
    }
}
