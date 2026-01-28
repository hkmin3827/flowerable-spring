package com.flowerable.spring.service.auth;

import com.flowerable.spring.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopAuthService {
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;

}
