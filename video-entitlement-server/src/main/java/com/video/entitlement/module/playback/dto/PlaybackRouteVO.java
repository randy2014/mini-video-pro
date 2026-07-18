package com.video.entitlement.module.playback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackRouteVO {
    private Long id;
    private String routeCode;
    private String providerCode;
    private String routeType;
    private String targetTemplate;
    private Integer priority;
    private Boolean enabled;
    private String authorizationStatus;
}
