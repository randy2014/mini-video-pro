package com.video.entitlement.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    /** 账号（复用手机号） */
    @NotBlank
    private String mobile;

    @NotBlank
    private String password;

    /** 权益码（必填，登录时绑定权益） */
    @NotBlank
    private String entitlementCode;

    /** 图形验证码标识，取自 /api/v1/auth/captcha */
    @NotBlank
    private String captchaKey;

    /** 图形验证码内容 */
    @NotBlank
    private String captchaCode;

    @NotBlank
    private String devicePublicId;

    @NotBlank
    private String clientType;

    private String appVersion;
}
