package com.video.entitlement.module.playback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackResolveRequest {
    @NotBlank
    private String platformCode;

    @NotBlank
    private String contentKey;

    private String canonicalUrl;
    private String originalUrl;
}
