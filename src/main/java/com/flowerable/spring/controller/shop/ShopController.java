package com.flowerable.spring.controller.shop;

import com.flowerable.spring.dto.shop.ShopDetailRes;
import com.flowerable.spring.security.CustomUserDetails;
import com.flowerable.spring.service.shop.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @GetMapping("/me")
    public ShopDetailRes me(@AuthenticationPrincipal CustomUserDetails details) {
        return shopService.getMyDetails(details.getId());
    }
}
