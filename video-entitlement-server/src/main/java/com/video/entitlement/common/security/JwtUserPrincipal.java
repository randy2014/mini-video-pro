package com.video.entitlement.common.security;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;

@Getter
@AllArgsConstructor
public class JwtUserPrincipal implements Principal {
    private final Long userId;
    private final String userNo;
    private final String role;
    private final Claims claims;

    @Override
    public String getName() {
        return userNo;
    }
}
