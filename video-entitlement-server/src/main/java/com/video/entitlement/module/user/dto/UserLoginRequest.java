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
    @NotBlank
    private String mobile;

    @NotBlank
    private String verificationCode;

    @NotBlank
    private String devicePublicId;

    @NotBlank
    private String clientType;

    private String appVersion;
}
