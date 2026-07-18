package com.video.entitlement.module.playback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackDecisionVO {
    private String requestId;
    private String decisionType;
    private String targetUrl;
    private Integer attemptNo;
    private Boolean hasNext;
    private String message;
}
