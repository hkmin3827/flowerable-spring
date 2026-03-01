package com.flowerable.spring.domain.user.service;

import com.flowerable.spring.domain.user.dto.UserDetailRes;
import com.flowerable.spring.domain.user.dto.UserUpdateInfoReq;
import com.flowerable.spring.domain.user.entity.User;
import com.flowerable.spring.global.exception.UserNotFoundException;
import com.flowerable.spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void updateUserInfo(Long accountId, UserUpdateInfoReq req){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);
        user.update(req);
    }

    @Transactional(readOnly = true)
    public UserDetailRes getMyDetails(Long accountId){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
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

    @Transactional(readOnly = true)
    public UserDetailRes getUserDetails(Long userId){
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
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
