    package com.flowerable.spring.config;

    import com.flowerable.spring.jwt.JwtAuthenticationFilter;
    import com.flowerable.spring.oauth2.handler.OAuth2LoginSuccessHandler;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.HttpStatusEntryPoint;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfigurationSource;

    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {
        private final CorsConfigurationSource corsConfigurationSource;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .formLogin(form -> form.disable())
                    .httpBasic(basic -> basic.disable())
                    .cors(cors -> cors.configurationSource(corsConfigurationSource))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/api/auth/login",
                                    "/api/auth/oauth/**",
                                    "/api/auth/reissue",
                                    "/oauth2/**",
                                    "/login/oauth2/**",
                                    "/api/auth/users/signup",
                                    "/api/auth/shops/signup",
                                    "/api/regions/**",
                                    "/ws-test.html",
                                    "/ws/**",
                                    "/sockjs/**"
                            ).permitAll()
                            .requestMatchers(
                                    "/api/auth/withdraw",
                                    "/api/auth/logout",
                                    "/api/users/**",
                                    "/api/shops/**",
                                    "/api/flowers/**",
                                    "/api/notifications/**",
                                    "/api/chats/**",
                                    "/api/shopimages/**"
                            ).authenticated()
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/shopflowers/**",
                                    "/api/orders/shops/**",
                                    "/api/my-shop/images/**").hasRole("SHOP")
                            .requestMatchers("/api/orders/users/**",
                                    "/api/cart/**").hasRole("USER")
                            .anyRequest().authenticated()
                    )
                    .exceptionHandling(ex ->
                            ex.authenticationEntryPoint(
                                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                            ).accessDeniedHandler((request, response, accessDeniedException) -> {
                                response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("""
                                {
                                  "code": "ACCESS_DENIED",
                                  "message": "접근 권한이 없습니다."
                                }
                                """);
                            })
                    )
                    .addFilterBefore(
                            jwtAuthenticationFilter,
                            UsernamePasswordAuthenticationFilter.class
                    )
                    // Tomcat이 HTTP 세션 ID를 생성하기 위해 SecureRandom(SHA1PRNG) 초기화
                    // 엔트로피 수집(특히 첫 초기화 시) 때문에 느리게 시작되는 경우가 흔함
                    // => sessionManagement ~ : 세션 아예 비활성화
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    ).oauth2Login(oauth -> oauth
                    .successHandler(oAuth2LoginSuccessHandler)
            );

            return http.build();
        }
    }
