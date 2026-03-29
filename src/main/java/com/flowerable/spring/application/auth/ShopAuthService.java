package com.flowerable.spring.application.auth;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.auth.constant.Provider;
import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.domain.shop.constant.ShopStatus;
import com.flowerable.spring.application.auth.dto.AuthReq;
import com.flowerable.spring.application.auth.dto.AuthRes;
import com.flowerable.spring.domain.auth.Account;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.SuspendedAccountException;
import com.flowerable.spring.global.exception.ShopNotFoundException;
import com.flowerable.spring.global.jwt.JwtProvider;
import com.flowerable.spring.global.jwt.RefreshTokenService;
import com.flowerable.spring.domain.auth.AccountRepository;
import com.flowerable.spring.domain.shop.ShopRepository;
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


    public AuthRes signup(AuthReq.ShopSignup dto){
        validateEmailOrTelnumDuplicated(dto.getEmail(), dto.getTelnum());

        Region region = Region.fromCode(dto.getRegionCode());
        District district = District.fromCode(dto.getDistrictCode());

        validateRegionDistrict(region, district);
        Account account = Account.createShopAccount(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getTelnum()
        );

        accountRepository.save(account);

        Shop shop = Shop.create(account, dto.getShopName(), dto.getAddress(), region, district);
        shopRepository.save(shop);
        return issue(account.getId(), Role.ROLE_SHOP, shop.getShopName(), Provider.LOCAL, account.getStatus(), shop.getStatus());
    }

    @Transactional(readOnly = true)
    public AuthRes login(AuthReq.Login dto) {
        Account account = accountRepository.findByEmail(dto.getEmail())
                .orElseThrow(ShopNotFoundException::new);

        if (account.getRole() != dto.getRole()) {
            throw new CustomException(ErrorCode.LOGIN_ROLE_MISMATCH);
        }

        if (account.getProvider() != Provider.LOCAL) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_TYPE);
        }

        if(account.getStatus() == AccountStatus.DELETED){
            throw new CustomException(ErrorCode.DELETED_ACCOUNT);
        }
        if (account.getStatus() == AccountStatus.SUSPENDED) {
            throw new SuspendedAccountException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(account.getId())
                .orElseThrow(ShopNotFoundException::new);

        return issue(account.getId(), Role.ROLE_SHOP, shop.getShopName(), Provider.LOCAL, account.getStatus(), shop.getStatus());
    }

    @Transactional
    public void withdraw(Long accountId, AuthReq.Withdraw dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(ShopNotFoundException::new);

        if (!"영구탈퇴임을 확인했습니다".equals(dto.getConfirmText())) {
            throw new IllegalArgumentException("입력하신 탈퇴 확인 문구가 틀립니다.");
        }
        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        shopRepository.findByAccountIdAndDeletedAtIsNull(accountId).ifPresent(Shop::softDelete);
        account.softDelete();

        refreshTokenService.deleteRefreshToken(accountId);
    }

    private AuthRes issue(Long accountId, Role role, String shopName, Provider provider, AccountStatus status, ShopStatus shopStatus) {
        String accessToken =
                jwtProvider.createAccessToken(accountId, role);

        String refreshToken =
                jwtProvider.createRefreshToken(accountId, role);

        refreshTokenService.saveRefreshToken(accountId, refreshToken);

        return AuthRes.builder()
                .id(accountId)
                .role(role)
                .name(shopName)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .provider(provider)
                .accountStatus(status)
                .shopStatus(shopStatus)
                .build();
    }

    private void validateRegionDistrict(Region region, District district){
        if(district == null || region == null){
            throw new CustomException(ErrorCode.INVALID_LOCATION);
        }

        if(!District.findByRegion(region).contains(district)){
            throw new CustomException(ErrorCode.INVALID_LOCATION);
        }
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
