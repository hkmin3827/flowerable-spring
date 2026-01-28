package com.flowerable.spring.jwt;

import com.flowerable.spring.constant.Role;
import com.flowerable.spring.constant.TokenType;

import com.flowerable.spring.security.CustomUserDetails;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String uri = request.getRequestURI();

        return uri.startsWith("/api/auth")
                || uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs");
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
                // 1️⃣ tokenType 검사 (ACCESS만 허용)
                if (jwtProvider.getTokenType(token) != TokenType.ACCESS) {
                    throw new JwtException("Not access token");
                }

                // 2️⃣ 블랙리스트 검사 (로그아웃된 토큰)
                String jti = jwtProvider.getJti(token);
                if (refreshTokenService.isAccessTokenBlacklisted(jti)) {
                    throw new JwtException("Blacklisted token");
                }

                // 3️⃣ Claim 파싱
                Long accountId = jwtProvider.getId(token);
                Role role = jwtProvider.getRole(token);


                // 4️⃣ CustomUserDetails 기반 Authentication 생성
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
        String header = req.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")){
            return header.substring(7);
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

    /**
     * Authentication 객체 생성
     */
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
