package com.video.entitlement.common.security;

import com.video.entitlement.common.constant.ApiConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey accessSecretKey;
    private final long accessExpirationMs;

    private final SecretKey refreshSecretKey;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.access-token.secret}") String accessSecret,
            @Value("${app.jwt.access-token.expiration-ms}") long accessExpirationMs,
            @Value("${app.jwt.refresh-token.secret}") String refreshSecret,
            @Value("${app.jwt.refresh-token.expiration-ms}") long refreshExpirationMs) {
        this.accessSecretKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(Long userId, String userNo, String role, Map<String, Object> extraClaims) {
        return generateToken(accessSecretKey, accessExpirationMs, userId, userNo, role, extraClaims);
    }

    public String generateRefreshToken(Long userId, String userNo, String role) {
        return generateToken(refreshSecretKey, refreshExpirationMs, userId, userNo, role, null);
    }

    public Claims parseAccessToken(String token) {
        return parseToken(accessSecretKey, token);
    }

    public Claims parseRefreshToken(String token) {
        return parseToken(refreshSecretKey, token);
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    private String generateToken(SecretKey key, long expirationMs, Long userId, String userNo, String role, Map<String, Object> extra) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userNo", userNo)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs));

        if (extra != null) {
            extra.forEach(builder::claim);
        }

        return builder.signWith(key).compact();
    }

    private Claims parseToken(SecretKey key, String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}
