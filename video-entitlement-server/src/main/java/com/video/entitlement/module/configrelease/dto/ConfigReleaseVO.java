package com.video.entitlement.module.configrelease.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigReleaseVO {
    private Long id;
    private String releaseNo;
    private String releaseType;
    private String configVersion;
    private String status;
    private Integer grayPercentage;
    private String description;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}
