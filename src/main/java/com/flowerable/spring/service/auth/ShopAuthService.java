package com.flowerable.spring.service.auth;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.region.District;
import com.flowerable.spring.constant.region.Region;
import com.flowerable.spring.constant.shop.ShopStatus;
import com.flowerable.spring.dto.auth.AuthReq;
import com.flowerable.spring.dto.auth.AuthRes;
import com.flowerable.spring.entity.account.Account;
import com.flowerable.spring.entity.shop.Shop;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.SuspendedAccountException;
import com.flowerable.spring.exception.ShopNotFoundException;
import com.flowerable.spring.jwt.JwtProvider;
import com.flowerable.spring.jwt.RefreshTokenService;
import com.flowerable.spring.repository.AccountRepository;
import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (accountRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
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
        Account account = accountRepository.findByEmailAndDeletedAtIsNull(dto.getEmail())
                .orElseThrow(ShopNotFoundException::new);

        if (account.getRole() != dto.getRole()) {
            throw new CustomException(ErrorCode.LOGIN_ROLE_MISMATCH);
        }

        if (account.getProvider() != Provider.LOCAL) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_TYPE);
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new SuspendedAccountException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        Shop shop = shopRepository.findByAccountIdAndDeletedAtIsNull(account.getId())
                .orElseThrow(ShopNotFoundException::new);

        if(shop.getStatus()== ShopStatus.SUSPENDED){
            throw new SuspendedAccountException();
        }

        return issue(account.getId(), Role.ROLE_SHOP, shop.getShopName(), Provider.LOCAL, account.getStatus(), shop.getStatus());
    }

    public void withdraw(Long accountId, AuthReq.Withdraw dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(ShopNotFoundException::new);

        if (!"탈퇴".equals(dto.getConfirmText())) {
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

        // Redis에 Refresh Token 저장
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
}
