package com.flowerable.spring.service.auth;

import com.flowerable.spring.constant.*;
import com.flowerable.spring.dto.auth.AuthReqDto;
import com.flowerable.spring.dto.auth.AuthResDto;
import com.flowerable.spring.entity.account.Account;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.InactiveAccountException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.jwt.JwtProvider;
import com.flowerable.spring.jwt.RefreshTokenService;
import com.flowerable.spring.repository.AccountRepository;
import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopAuthService {
    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthResDto signup(AuthReqDto.ShopSignup dto){
        if (accountRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
        Account account = Account.createShopAccount(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword())
        );

        accountRepository.save(account);

        Shop shop = Shop.create(account, dto.getShopName(), dto.getAddress(), dto.getTelnum());
        shopRepository.save(shop);

        return issue(account.getId(), Role.ROLE_SHOP);
    }

    @Transactional(readOnly = true)
    public AuthResDto login(AuthReqDto.Login dto) {
        Account account = accountRepository.findByEmailAndDeletedAtIsNull(dto.getEmail())
                .orElseThrow(ShopNotFoundException::new);

        if (account.getRole() != dto.getLoginType()) {
            throw new CustomException(ErrorCode.LOGIN_ROLE_MISMATCH);
        }

        if (account.getProvider() != Provider.LOCAL) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_TYPE);
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(account.getId())
                .orElseThrow(ShopNotFoundException::new);

        if(shop.getStatus()== ShopStatus.SUSPENDED){
            throw new InactiveAccountException();
        }

        return issue(account.getId(), Role.ROLE_SHOP);
    }

    public void withdraw(Long accountId, AuthReqDto.Withdraw dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(ShopNotFoundException::new);

        if (!"탈퇴".equals(dto.getConfirmText())) {
            throw new IllegalArgumentException("입력하신 탈퇴 확인 문구가 틀립니다.");
        }
        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        shopRepository.findByAccountId(accountId).ifPresent(Shop::softDelete);
        account.softDelete();

        refreshTokenService.deleteRefreshToken(accountId);    }

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
