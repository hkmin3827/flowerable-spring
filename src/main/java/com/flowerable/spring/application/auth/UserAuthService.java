package com.flowerable.spring.application.auth;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.auth.constant.Provider;
import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.application.auth.dto.AuthReq;
import com.flowerable.spring.application.auth.dto.AuthRes;
import com.flowerable.spring.domain.user.User;
import com.flowerable.spring.domain.auth.Account;
import com.flowerable.spring.global.exception.AccountNotFoundException;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.SuspendedAccountException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import com.flowerable.spring.infra.kakao.KakaoUnlinkClient;
import com.flowerable.spring.global.jwt.JwtProvider;
import com.flowerable.spring.global.jwt.RefreshTokenService;
import com.flowerable.spring.domain.auth.AccountRepository;
import com.flowerable.spring.domain.user.UserRepository;
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

    public AuthRes signup(AuthReq.UserSignup dto){
        validateEmailOrTelnumDuplicated(dto.getEmail(), dto.getTelnum());

        Account account = Account.createUserAccount(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelnum()
        );
        accountRepository.save(account);

        User user = User.create(account, dto.getName());
        userRepository.save(user);

        return issue(account.getId(), Role.ROLE_USER, user.getName(), account.getEmail(), Provider.LOCAL, account.getStatus(), null);
    }

    @Transactional(readOnly = true)
    public AuthRes login(AuthReq.Login dto){
        Account account = accountRepository.findByEmail(dto.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (account.getProvider() != Provider.LOCAL) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_TYPE);
        }

        if (account.getRole() != dto.getRole()) {
            throw new CustomException(ErrorCode.LOGIN_ROLE_MISMATCH);
        }
        if(account.getStatus() == AccountStatus.DELETED){
            throw new CustomException(ErrorCode.DELETED_ACCOUNT);
        }
        if (account.getStatus() == AccountStatus.SUSPENDED) {
            throw new SuspendedAccountException();
        }

        User user = userRepository.findByAccountIdAndDeletedAtIsNull(account.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return issue(account.getId(), account.getRole(), user.getName(), account.getEmail(), Provider.LOCAL, account.getStatus(), user.getProfileImageUrl());
    }


    @Transactional
    public AuthRes processOAuthLogin(Provider provider, String providerId) {

        Account account = accountRepository
                .findByProviderAndProviderIdAndDeletedAtIsNull(provider, providerId)
                .orElseGet(() -> createOAuthAccount(provider, providerId));

        if (account.getStatus() == AccountStatus.SUSPENDED ||
                account.getStatus() == AccountStatus.DELETED) {
            throw new SuspendedAccountException();
        }

        User user = userRepository
                .findByAccountId(account.getId())
                .orElseGet(() -> userRepository.save(User.create(account, null)));

        // 필수 정보 미완성 → TEMP
        if (account.getEmail() == null ||
                account.getTelnum() == null ||
                user.getName() == null) {

            account.markTemp();

            return AuthRes.requireEmailAndTelnum(
                    account.getId(),
                    provider,
                    AccountStatus.TEMP
            );
        }

        return issue(
                account.getId(),
                account.getRole(),
                user.getName(),
                account.getEmail(),
                account.getProvider(),
                account.getStatus(),
                user.getProfileImageUrl()
        );
    }


    @Transactional
    private Account createOAuthAccount(Provider provider, String providerId) {
        Account account = Account.createOAuth(
                provider,
                providerId,
                Role.ROLE_USER
        );

        account.markTemp();
        return accountRepository.save(account);
    }

    @Transactional
    public AuthRes completeOAuthSignup(AuthReq.OAuthComplete req) {
        Account account = accountRepository.findById(req.accountId())
                .orElseThrow(AccountNotFoundException::new);

        if (account.getStatus() != AccountStatus.TEMP) {
            throw new CustomException(ErrorCode.INVALID_ACCOUNT_STATUS);
        }

        validateEmailOrTelnumDuplicated(req.email(), req.telnum());

        User user = userRepository.findByAccountId(account.getId())
                .orElseGet(() -> userRepository.save(User.create(account, req.name())));

        account.setEmail(req.email());
        account.setTelnum(req.telnum());
        account.activate();
        user.setName(req.name());

        return issue(
                account.getId(),
                account.getRole(),
                user.getName(),
                account.getEmail(),
                account.getProvider(),
                account.getStatus(),
                null
        );
    }

    @Transactional
    public void withdraw(Long accountId, AuthReq.Withdraw dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(UserNotFoundException::new);

        if (!"영구탈퇴임을 확인했습니다".equals(dto.getConfirmText())) {
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

        userRepository.findByAccountIdAndDeletedAtIsNull(accountId).ifPresent(User::softDelete);

        account.softDelete();

        refreshTokenService.deleteRefreshToken(accountId);
    }


    private AuthRes issue(Long accountId, Role role, String name, String email, Provider provider, AccountStatus status, String profileImageUrl) {
        String accessToken =
                jwtProvider.createAccessToken(accountId, role, email);

        String refreshToken =
                jwtProvider.createRefreshToken(accountId, role, email);

        refreshTokenService.saveRefreshToken(accountId, refreshToken);

        return AuthRes.builder()
                .id(accountId)
                .role(role)
                .name(name)
                .profileImgUrl(profileImageUrl)
                .accountStatus(status)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .provider(provider)
                .shopStatus(null)
                .build();
    }

    private void validateEmailOrTelnumDuplicated(String email, String telnum) {
        if (email != null &&
                accountRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        if (telnum != null &&
                accountRepository.existsByTelnumAndDeletedAtIsNull(telnum)) {
            throw new CustomException(ErrorCode.TELNUM_DUPLICATED);
        }
    }
}
