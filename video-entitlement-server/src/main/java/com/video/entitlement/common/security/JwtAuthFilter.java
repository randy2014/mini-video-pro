package com.video.entitlement.common.security;

import com.video.entitlement.common.constant.ApiConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(ApiConstants.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(ApiConstants.BEARER_PREFIX)) {
            String token = authHeader.substring(ApiConstants.BEARER_PREFIX.length());
            try {
                Claims claims = jwtTokenProvider.parseAccessToken(token);
                String userId = claims.getSubject();
                String userNo = claims.get("userNo", String.class);
                String role = claims.get("role", String.class);

                List<SimpleGrantedAuthority> authorities = role != null
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        : Collections.emptyList();

                JwtUserPrincipal principal = new JwtUserPrincipal(
                        Long.parseLong(userId), userNo, role, claims);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                log.debug("JWT expired: {}", e.getMessage());
            } catch (JwtException e) {
                log.debug("JWT invalid: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
