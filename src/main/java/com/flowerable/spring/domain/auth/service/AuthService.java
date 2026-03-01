package com.flowerable.spring.domain.auth.service;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.domain.auth.constant.TokenType;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import com.flowerable.spring.domain.auth.dto.AuthReq;
import com.flowerable.spring.domain.auth.dto.AuthRes;
import com.flowerable.spring.domain.auth.entity.Account;
import com.flowerable.spring.domain.shop.entity.Shop;
import com.flowerable.spring.domain.user.entity.User;
import com.flowerable.spring.global.exception.AccountNotFoundException;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.ShopNotFoundException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import com.flowerable.spring.global.jwt.JwtProvider;
import com.flowerable.spring.global.jwt.RefreshTokenService;
import com.flowerable.spring.domain.auth.repository.AccountRepository;
import com.flowerable.spring.domain.shop.repository.ShopRepository;
import com.flowerable.spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// facade
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserAuthService userAuthService;
    private final ShopAuthService shopAuthService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;


    @Transactional(readOnly = true)
    public AuthRes login(AuthReq.Login dto) {
        if (dto.getRole() == Role.ROLE_USER || dto.getRole() == Role.ROLE_ADMIN) {
            return userAuthService.login(dto);
        }

        if (dto.getRole() == Role.ROLE_SHOP) {
            return shopAuthService.login(dto);
        }
        throw new CustomException(ErrorCode.INVALID_ROLE);
    }

    public void withdraw(Long accountId, Role role, AuthReq.Withdraw dto) {

        if (role == Role.ROLE_ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_WITHDRAW_NOT_ALLOWED);
        }

        if (role == Role.ROLE_USER) {
            userAuthService.withdraw(accountId, dto);
            return;
        }

        if (role == Role.ROLE_SHOP) {
            shopAuthService.withdraw(accountId, dto);
            return;
        }
        throw new CustomException(ErrorCode.INVALID_ROLE);
    }

    public AuthRes reissue(String refreshToken) {

        if (jwtProvider.getTokenType(refreshToken) != TokenType.REFRESH) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long accountId = jwtProvider.getId(refreshToken);
        Role role = jwtProvider.getRole(refreshToken);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new CustomException(ErrorCode.INVALID_ACCOUNT_STATUS);
        }

        if (!refreshTokenService.validateRefreshToken(accountId, refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken =
                jwtProvider.createAccessToken(accountId, role);

        String newRefreshToken =
                jwtProvider.createRefreshToken(accountId, role);

        refreshTokenService.saveRefreshToken(accountId, newRefreshToken);

        String profileImgUrl = null;
        ShopStatus shopStatus = null;

        if (role == Role.ROLE_USER) {
            User user = userRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                    .orElseThrow(UserNotFoundException::new);

            profileImgUrl = user.getProfileImageUrl();
        }

        if (role == Role.ROLE_SHOP) {
            Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(accountId)
                    .orElseThrow(ShopNotFoundException::new);

            shopStatus = shop.getStatus();
        }

        return AuthRes.builder()
                .id(accountId)
                .role(role)
                .accountStatus(account.getStatus())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .profileImgUrl(profileImgUrl)
                .shopStatus(shopStatus)
                .build();
    }

    public void logout(Long accountId) {
            refreshTokenService.deleteRefreshToken(accountId);
        }

}


