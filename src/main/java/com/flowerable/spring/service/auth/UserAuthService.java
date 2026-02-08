package com.flowerable.spring.service.auth;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.dto.auth.*;
import com.flowerable.spring.entity.user.User;
import com.flowerable.spring.entity.account.Account;
import com.flowerable.spring.exception.AccountNotFoundException;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.SuspendedAccountException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.infra.kakao.KakaoUnlinkClient;
import com.flowerable.spring.jwt.JwtProvider;
import com.flowerable.spring.jwt.RefreshTokenService;
import com.flowerable.spring.repository.AccountRepository;
import com.flowerable.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (accountRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        Account account = Account.createUserAccount(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelnum()
        );
        accountRepository.save(account);

        User user = User.create(account, dto.getName());
        userRepository.save(user);

        return issue(account.getId(), Role.ROLE_USER, dto.getName(), Provider.LOCAL);
    }

    @Transactional(readOnly = true)
    public AuthRes login(AuthReq.Login dto){
        Account account = accountRepository.findByEmailAndDeletedAtIsNull(dto.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (account.getRole() != dto.getRole()) {
            throw new CustomException(ErrorCode.LOGIN_ROLE_MISMATCH);
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new SuspendedAccountException();
        }

        if (account.getProvider() != Provider.LOCAL) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_TYPE);
        }

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        User user = userRepository.findByAccountIdAndDeletedAtIsNull(account.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return issue(account.getId(), account.getRole(), user.getName(), Provider.LOCAL);
    }

    @Transactional
    public AuthRes signupOrLoginOAuth2(AuthReq.OAuth2Login dto){
        Account account = accountRepository
                .findByProviderAndProviderIdAndDeletedAtIsNull(dto.getProvider(), dto.getProviderId())
                .orElseGet(() -> createOAuthAccount(dto));

        userRepository.findByAccountId(account.getId())
                .orElseGet(() -> userRepository.save(User.create(account, dto.getName())));

        if (account.getStatus() == AccountStatus.SUSPENDED || account.getStatus() == AccountStatus.DELETED) {
            throw new SuspendedAccountException();
        }

        // email 미확정 → 토큰 발급 X 또는 제한 토큰
        if (account.getStatus() == AccountStatus.TEMP) {
            return AuthRes.requireEmailAndTelnum(
                    account.getId(),
                    dto.getProvider()
            );
        }

        return issue(account.getId(), account.getRole(), dto.getName(), dto.getProvider());
    }

    private Account createOAuthAccount(AuthReq.OAuth2Login dto) {

        Account account = Account.createOAuth(
                dto.getProvider(),
                dto.getProviderId(),
                Role.ROLE_USER
        );

        if (dto.getEmail() != null || dto.getTelnum() != null) {
            validateEmailOrTelnumDuplicated(dto.getEmail(), dto.getTelnum());
            account.setEmail(dto.getEmail());
            account.setTelnum(dto.getTelnum());
            account.activate();
        } else {
            account.markTemp();
        }

        return accountRepository.save(account);
    }

    @Transactional
    public AuthRes completeOAuthSignup(AuthReq.OAuthComplete req) {

        Account account = accountRepository.findById(req.accountId())
                .orElseThrow(AccountNotFoundException::new);

        if (account.getStatus() != AccountStatus.TEMP) {
            throw new CustomException(ErrorCode.INVALID_ACCOUNT_STATUS);
        }

        validateEmailOrTelnumDuplicated(req.email(), req.telnum()); // 여기서 터지면 전체 롤백

        account.setEmail(req.email());
        account.setTelnum(req.telnum());
        account.activate();

        return issue(
                account.getId(),
                account.getRole(),
                account.getEmail(),
                account.getProvider()
        );
    }

    @Transactional
    public void withdraw(Long accountId, AuthReq.Withdraw dto) {
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

        userRepository.findByAccountIdAndDeletedAtIsNull(accountId).ifPresent(User::softDelete);

        account.softDelete();

        refreshTokenService.deleteRefreshToken(accountId);
    }

    private AuthRes issue(Long accountId, Role role, String name, Provider provider) {
        String accessToken =
                jwtProvider.createAccessToken(accountId, role);

        String refreshToken =
                jwtProvider.createRefreshToken(accountId, role);

        // Redis에 Refresh Token 저장
        refreshTokenService.saveRefreshToken(accountId, refreshToken);

        return AuthRes.builder()
                .id(accountId)
                .role(role)
                .name(name)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .provider(provider)
                .build();
    }

    private void validateEmailOrTelnumDuplicated(String email, String telnum) {

        if (email != null &&
                accountRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        if (telnum != null &&
                accountRepository.existsByTelnumAndDeletedAtIsNull(telnum)) {
            throw new CustomException(ErrorCode.TELNUM_DUPLICATED);
        }
    }
}
