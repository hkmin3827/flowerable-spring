package com.flowerable.spring.service.auth;

import com.flowerable.spring.constant.ErrorCode;
import com.flowerable.spring.constant.Role;
import com.flowerable.spring.constant.TokenType;
import com.flowerable.spring.dto.auth.AuthReq;
import com.flowerable.spring.dto.auth.AuthRes;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.jwt.JwtProvider;
import com.flowerable.spring.jwt.RefreshTokenService;
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


    @Transactional(readOnly = true)
    public AuthRes login(AuthReq.Login dto) {
        if (dto.getLoginType() == Role.ROLE_USER || dto.getLoginType() == Role.ROLE_ADMIN) {
            return userAuthService.login(dto);
        }

        if (dto.getLoginType() == Role.ROLE_SHOP) {
            return shopAuthService.login(dto);
        }
        throw new CustomException(ErrorCode.INVALID_ROLE);
    }

    @Transactional(readOnly = true)
    public AuthRes oauth2Login(AuthReq.OAuth2Login dto){
        return userAuthService.signupOrLoginOAuth2(dto);
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

        if (!refreshTokenService.validateRefreshToken(accountId, refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken =
                jwtProvider.createAccessToken(accountId, role);


        String newRefreshToken =
                jwtProvider.createRefreshToken(accountId, role);

        refreshTokenService.saveRefreshToken(accountId, newRefreshToken);

        return AuthRes.builder()
                .id(accountId)
                .role(role)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

        public void logout(Long accountId) {
            refreshTokenService.deleteRefreshToken(accountId);
        }
}
