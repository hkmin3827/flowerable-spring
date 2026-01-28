package com.flowerable.spring.service.auth;

import com.flowerable.spring.constant.ErrorCode;
import com.flowerable.spring.constant.Provider;
import com.flowerable.spring.dto.auth.LoginReqDto;
import com.flowerable.spring.dto.auth.UserSignupReqDto;
import com.flowerable.spring.dto.auth.WithdrawReqDto;
import com.flowerable.spring.entity.User;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.oauth2.userInfo.OAuth2UserInfo;
import com.flowerable.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignupReqDto dto){
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        User user = User.createLocalUser(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getName(),
                dto.getTelnum()
        );
        // address 는 선택값. address 제외한 필드는 메서드 오버라이드로 안전하게 객체 생성
        if (dto.getAddress() != null) {
            user.updateAddress(dto.getAddress());
        }

        userRepository.save(user);
    }

    public User login(LoginReqDto dto){
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        return user;
    }

    @Transactional
    public User signupOrLoginOAuth2(OAuth2UserInfo info){
        Optional<User> optionalUser =
                userRepository.findByProviderAndProviderId(
                        info.getProvider(),
                        info.getProviderId()
                );

        if (optionalUser.isEmpty()) {
            User newUser = User.createOAuthUser(
                    info.getProvider(),
                    info.getProviderId()
            );

            newUser.initOAuthInfo(
                    info.getEmail(),
                    info.getName()
            );

            return userRepository.save(newUser);
        }
        return optionalUser.get();
    }

    @Transactional
    public void withdraw(User user, WithdrawReqDto dto) {
        if (dto == null || !"탈퇴합니다".equals(dto.getConfirmText())) {
            throw new IllegalArgumentException("탈퇴 확인 문구가 올바르지 않습니다.");
        }

        // 카카오면 제공자랑 계정 연결까지 해제
        if (user.getProvider() == Provider.KAKAO) {
            try {
                kakaoUnlinkClient.unlink(user.getOauthId());
            } catch (Exception e) {
                log.warn("Kakao unlink failed", e);
            }
        }

        // 삭제
        userRepository.delete(user);
        // → DB에서 ON DELETE CASCADE로 연관 엔티티 자동 삭제
    }
}
