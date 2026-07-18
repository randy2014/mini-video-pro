package com.video.entitlement.module.playback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackReportRequest {
    @NotBlank
    private String requestId;

    @NotNull
    private Integer attemptNo;

    @NotBlank
    private String result;

    private String errorType;
    private Long durationMs;
}
