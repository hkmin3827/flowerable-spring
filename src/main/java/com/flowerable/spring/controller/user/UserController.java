package com.flowerable.spring.controller.user;

import com.flowerable.spring.dto.user.UserDetailRes;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDetailRes me(@AuthenticationPrincipal CustomUserDetails details) {

        return userService.getMyDetails(details.getId());
    }
}
