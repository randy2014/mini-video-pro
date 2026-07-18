package com.video.entitlement.module.entitlement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String devicePublicId;
}
