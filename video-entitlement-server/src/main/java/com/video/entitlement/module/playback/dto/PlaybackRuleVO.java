package com.video.entitlement.module.playback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackRuleVO {
    private Long id;
    private String platformCode;
    private String clientType;
    private String versionRange;
    private Long routeGroupId;
    private Integer priority;
    private Boolean enabled;
}
