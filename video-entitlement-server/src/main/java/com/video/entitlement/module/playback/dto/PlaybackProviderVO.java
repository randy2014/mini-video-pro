package com.video.entitlement.module.playback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackProviderVO {
    private Long id;
    private String providerCode;
    private String providerName;
    private String providerType;
    private String status;
    private String authorizationStatus;
}
