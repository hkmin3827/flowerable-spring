package com.flowerable.spring.service.auth;

import com.flowerable.spring.constant.*;
import com.flowerable.spring.dto.auth.*;
import com.flowerable.spring.entity.User;
import com.flowerable.spring.entity.account.Account;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.InactiveAccountException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.infra.kakao.KakaoUnlinkClient;
import com.flowerable.spring.jwt.JwtProvider;
import com.flowerable.spring.jwt.RefreshTokenService;
import com.flowerable.spring.repository.AccountRepository;
import com.flowerable.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoUnlinkClient kakaoUnlinkClient;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AccountRepository accountRepository;

    public AuthResDto signup(AuthReqDto.Signup dto){
        if (accountRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        Account account = Account.createLocal(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                Role.ROLE_USER
        );
        accountRepository.save(account);

        User user = User.create(account, dto.getName(), dto.getTelnum());
        if (dto.getAddress() != null) user.updateAddress(dto.getAddress());
        // address 는 선택값. address 제외한 필드는 메서드 오버라이드로 안전하게 객체 생성

        userRepository.save(user);
        return issue(account.getId(), Role.ROLE_USER);
    }

    @Transactional(readOnly = true)
    public AuthResDto login(AuthReqDto.Login dto){
        Account account = accountRepository.findByEmailAndDeletedAtIsNull(dto.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (account.getRole() != dto.getLoginType()) {
            throw new CustomException(ErrorCode.LOGIN_ROLE_MISMATCH);
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException();
        }

        if (account.getProvider() != Provider.LOCAL) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_TYPE);
        }

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        User user = userRepository.findByAccountIdAndDeletedAtIsNull(account.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.isActive()) {
            throw new InactiveAccountException();
        }


        return issue(account.getId(), account.getRole());
    }

    @Transactional
    public AuthResDto signupOrLoginOAuth2(AuthReqDto.OAuth2Login dto){
        Account account = accountRepository
                .findByProviderAndProviderIdAndDeletedAtIsNull(dto.getProvider(), dto.getProviderId())
                .orElseGet(() -> {
                    Account newAcc = Account.createOAuth(dto.getProvider(), dto.getProviderId(), Role.ROLE_USER);
                    newAcc.setEmailIfPresent(dto.getEmail());
                    return accountRepository.save(newAcc);
                });

        userRepository.findByAccountId(account.getId())
                .orElseGet(() -> userRepository.save(User.create(account, dto.getName(), null)));

        if (account.getStatus() != AccountStatus.ACTIVE) throw new InactiveAccountException();

        return issue(account.getId(), account.getRole());
    }

    @Transactional
    public void withdraw(Long accountId, AuthReqDto.Withdraw dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(UserNotFoundException::new);


        if (!"탈퇴".equals(dto.getConfirmText())) {
            throw new IllegalArgumentException("입력하신 탈퇴 확인 문구가 틀립니다.");
        }
        if (account.getProvider() == Provider.LOCAL &&
                !passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (account.getProvider() == Provider.KAKAO) {
            try {
                kakaoUnlinkClient.unlink(account.getProviderId());
            } catch (Exception e) {
                log.warn("Kakao unlink failed", e);
            }
        }

        userRepository.findByAccountId(accountId).ifPresent(User::softDelete);

        account.softDelete();

        refreshTokenService.deleteRefreshToken(accountId);
    }

    private AuthResDto issue(Long accountId, Role role) {
        String accessToken =
                jwtProvider.createAccessToken(accountId, role);

        String refreshToken =
                jwtProvider.createRefreshToken(accountId, role);

        // Redis에 Refresh Token 저장
        refreshTokenService.saveRefreshToken(accountId, refreshToken);

        return AuthResDto.builder()
                .id(accountId)
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
