package com.flowerable.spring.service.user;

import com.flowerable.spring.dto.user.UserDetailRes;
import com.flowerable.spring.entity.User;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailRes  getMyDetails(Long accountId){
        User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                .orElseThrow(UserNotFoundException::new);

        return UserDetailRes.builder()
                .id(user.getId())
                .email(user.getAccount().getEmail())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .name(user.getName())
                .deletedAt(user.getDeletedAt())
                .telnum(user.getTelnum())
                .active(user.isActive())
                .provider(user.getAccount().getProvider())
                .providerId(user.getAccount().getProviderId())
                .build();
    }
}
