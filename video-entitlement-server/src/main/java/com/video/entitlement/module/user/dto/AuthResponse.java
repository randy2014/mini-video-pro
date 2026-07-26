package com.video.entitlement.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserVO user;
    private Long expiresIn;
    /** 用户权益列表（仅含到期时间，供 App 到期提醒用） */
    private List<EntitlementInfo> entitlements;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntitlementInfo {
        private String entitlementCode;
        private LocalDateTime expireTime; // null = 永久
    }
}
