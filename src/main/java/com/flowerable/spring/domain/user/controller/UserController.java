package com.flowerable.spring.domain.user.controller;

import com.flowerable.spring.domain.user.dto.UserDetailRes;
import com.flowerable.spring.domain.user.dto.UserUpdateInfoReq;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDetailRes me(@AuthenticationPrincipal CustomUserDetails details) {
        return userService.getMyDetails(details.getId());
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateInfoReq req
            ){
        userService.updateUserInfo(userDetails.getId(), req);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public UserDetailRes getUserDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userId){
        return userService.getUserDetails(userId);

    }
}
