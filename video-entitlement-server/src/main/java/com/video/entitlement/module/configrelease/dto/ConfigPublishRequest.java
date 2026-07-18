package com.video.entitlement.module.configrelease.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigPublishRequest {
    @NotBlank
    private String releaseType;

    private String description;
    private Integer grayPercentage;
}
