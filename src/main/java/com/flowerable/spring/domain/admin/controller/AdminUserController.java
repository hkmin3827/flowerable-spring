package com.flowerable.spring.controller.admin;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.dto.admin.AdminUserListRes;
import com.flowerable.spring.dto.common.PageResponse;
import com.flowerable.spring.dto.user.UserDetailRes;
import com.flowerable.spring.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;
    @GetMapping
    public PageResponse<AdminUserListRes> getUsersByStatus(
            @RequestParam(required = false) AccountStatus accountStatus,
            @PageableDefault(size = 20, sort = "id")
            Pageable page
            ){
        return PageResponse.from(adminUserService.getUsersByStatus(accountStatus, page));
    }

    @GetMapping("/search")
    public PageResponse<AdminUserListRes> searchUsers(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "id")
            Pageable page
    ){
        return PageResponse.from(adminUserService.searchUsers(keyword, page));
    }

    @GetMapping("/{userId}")
    public UserDetailRes getUserDetails(@PathVariable Long userId)
    {
        return adminUserService.getUserDetails(userId);
    }

    @PatchMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUserAccount(
            @PathVariable Long userId
    ){
        adminUserService.changeStatus(userId, AccountStatus.ACTIVE);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/suspend")
    public ResponseEntity<Void> suspendUserAccount(
            @PathVariable Long userId
    ){
        adminUserService.changeStatus(userId, AccountStatus.SUSPENDED);

        return ResponseEntity.noContent().build();
    }


}
