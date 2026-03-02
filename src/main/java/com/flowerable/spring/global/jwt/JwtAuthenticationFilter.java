package com.flowerable.spring.global.jwt;

import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.domain.auth.constant.TokenType;

import com.flowerable.spring.global.security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.equals("/api/auth/login")
                || uri.equals("/api/auth/users/signup")
                || uri.equals("/api/auth/shops/signup")
                || uri.startsWith("/api/auth/oauth")
                || uri.equals("/api/auth/reissue")
                || uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs")
                || uri.equals("/ws-test.html")
                || uri.startsWith("/ws/")
                || uri.endsWith(".html")
                || uri.startsWith("/oauth2/")
                || uri.startsWith("/login/oauth2/")
                || uri.equals("/favicon.ico")
                || uri.equals("/error")
                || uri.startsWith("/api/notifications/subscribe");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain
    ) throws ServletException, IOException{

        String token = resolveToken(req);

        if (token != null) {
            try {
                if (jwtProvider.getTokenType(token) != TokenType.ACCESS) {
                    throw new JwtException("Not access token");
                }

                // 블랙리스트 검사 (추후 accessToken 블랙리스트 관리 추가 시 확장 가능, 현재 사용은 X)
                String jti = jwtProvider.getJti(token);
                if (refreshTokenService.isAccessTokenBlacklisted(jti)) {
                    throw new JwtException("Blacklisted token");
                }

                Long accountId = jwtProvider.getId(token);
                Role role = jwtProvider.getRole(token);


                UsernamePasswordAuthenticationToken authentication =
                        createAuthentication(accountId, role, req);

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                // Access Token 만료
                sendUnauthorizedResponse(res);
                return;

            } catch (JwtException | IllegalArgumentException e) {
                // 위조 / 잘못된 토큰 / 블랙리스트
                sendUnauthorizedResponse(res);
                return;
            }
        }

        filterChain.doFilter(req, res);

    }

    private String resolveToken(HttpServletRequest req){
        String bearerToken = req.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }

        String tokenParam = req.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        res.getWriter().write("""
            {
              "code": "UNAUTHORIZED",
              "message": "유효하지 않거나 만료된 토큰입니다."
            }
            """);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(
            Long accountId,
            Role role,
            HttpServletRequest request
    ) {
        CustomUserDetails userDetails =
                new CustomUserDetails(accountId, role);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        return authentication;
    }
}
