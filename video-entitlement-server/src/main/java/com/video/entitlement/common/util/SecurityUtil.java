package com.video.entitlement.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.video.entitlement.common.security.JwtUserPrincipal;

import java.util.Optional;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static Optional<JwtUserPrincipal> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser()
                .map(JwtUserPrincipal::getUserId)
                .orElseThrow(() -> new RuntimeException("Not authenticated"));
    }

    public static String getCurrentUserNo() {
        return getCurrentUser()
                .map(JwtUserPrincipal::getUserNo)
                .orElseThrow(() -> new RuntimeException("Not authenticated"));
    }
}
