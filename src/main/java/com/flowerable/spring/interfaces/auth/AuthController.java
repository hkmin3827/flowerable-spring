package com.flowerable.spring.interfaces.auth;

import com.flowerable.spring.application.auth.dto.AuthReq;
import com.flowerable.spring.application.auth.dto.AuthRes;
import com.flowerable.spring.application.auth.dto.PasswordForgotReq;
import com.flowerable.spring.application.auth.dto.PasswordResetReq;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.application.auth.AuthService;
import com.flowerable.spring.application.auth.ShopAuthService;
import com.flowerable.spring.application.auth.UserAuthService;
import com.flowerable.spring.application.auth.PasswordResetService;
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
    private final PasswordResetService passwordResetService;

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

    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody PasswordForgotReq req
    ) {
        passwordResetService.sendResetLink(req.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody PasswordResetReq req
    ) {
        passwordResetService.resetPassword(
                req.getToken(),
                req.getNewPassword()
        );
        return ResponseEntity.ok().build();
    }
}
