package com.flowerable.spring.controller.auth;

import com.flowerable.spring.dto.auth.AuthReq;
import com.flowerable.spring.dto.auth.AuthRes;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.auth.AuthService;
import com.flowerable.spring.service.auth.ShopAuthService;
import com.flowerable.spring.service.auth.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserAuthService userAuthService;
    private final ShopAuthService shopAuthService;

    @PostMapping("/users/signup")
    public AuthRes userSignup(
            @Valid @RequestBody AuthReq.UserSignup dto
    ) {
        return userAuthService.signup(dto);
    }

    @PostMapping("/shops/signup")
    public AuthRes shopSignup(
            @Valid @RequestBody AuthReq.ShopSignup dto
    ) {
        return shopAuthService.signup(dto);
    }

    @PostMapping("/login")
    public AuthRes login(@RequestBody AuthReq.Login req) {
        return authService.login(req);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AuthReq.Withdraw req
    ) {
        authService.withdraw(userDetails.getId(), userDetails.getRole(), req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        authService.logout(userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reissue")
    public AuthRes reissue(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return authService.reissue(refreshToken);
    }
}
