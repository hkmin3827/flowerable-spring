    package com.flowerable.spring.global.config;

    import com.flowerable.spring.global.jwt.JwtAuthenticationFilter;
    import com.flowerable.spring.handler.OAuth2LoginSuccessHandler;
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
                                    "/api/auth/password/forgot",
                                    "/api/auth/password/reset",
                                    "/oauth2/**",
                                    "/login/oauth2/**",
                                    "/api/auth/users/signup",
                                    "/api/auth/shops/signup",
                                    "/api/regions/**",
                                    "/ws-test.html",
                                    "/ws/**",
                                    "/sockjs/**",
                                    "/error",
                                    "/favicon.ico",
                                    "/api/notifications/subscribe/**"
                            ).permitAll()
                            .requestMatchers(
                                    "/api/auth/withdraw",
                                    "/api/auth/logout",
                                    "/api/users/**",
                                    "/api/shops/**",
                                    "/api/flowers/**",
                                    "/api/notifications/**",
                                    "/api/chats/**",
                                    "/api/shopimages/**",
                                    "/api/s3/**",
                                    "/api/payments/confirm"
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
                                response.setStatus(HttpStatus.FORBIDDEN.value());
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
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    ).oauth2Login(oauth -> oauth
                    .successHandler(oAuth2LoginSuccessHandler)
            );

            return http.build();
        }
    }
