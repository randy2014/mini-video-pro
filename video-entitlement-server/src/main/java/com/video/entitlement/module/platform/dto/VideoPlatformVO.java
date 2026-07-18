package com.video.entitlement.module.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoPlatformVO {
    private String platformCode;
    private String platformName;
    private String platformType;
    private String homeUrl;
    private String status;
    private List<String> domains;
}
