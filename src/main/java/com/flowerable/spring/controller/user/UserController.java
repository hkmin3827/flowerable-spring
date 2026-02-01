package com.flowerable.spring.controller.user;

import com.flowerable.spring.dto.user.UserDetailRes;
import com.flowerable.spring.dto.user.UserUpdateInfoReq;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.user.UserService;
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
            @RequestBody UserUpdateInfoReq req
            ){
        userService.updateUserInfo(userDetails.getId(), req);

        return ResponseEntity.noContent().build();
    }
}
