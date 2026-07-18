package com.video.entitlement.module.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformConfigRequest {
    @NotBlank
    private String platformCode;

    private String platformName;
    private String homeUrl;
    private List<String> domains;
}
