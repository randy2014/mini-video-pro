package com.video.entitlement.module.playback.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.security.JwtUserPrincipal;
import com.video.entitlement.module.playback.dto.*;
import com.video.entitlement.module.playback.service.PlaybackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "播放路由")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/api/v1/playback")
@RequiredArgsConstructor
public class PlaybackController {
    private final PlaybackService playbackService;

    @Operation(summary = "解析播放决策")
    @PostMapping("/resolve")
    public ApiResponse<PlaybackDecisionVO> resolve(@Valid @RequestBody PlaybackResolveRequest request,
                                                    @AuthenticationPrincipal JwtUserPrincipal principal) {
        return ApiResponse.success(playbackService.resolve(principal.getUserId(), request));
    }

    @Operation(summary = "上报播放结果")
    @PostMapping("/report")
    public ApiResponse<PlaybackDecisionVO> report(@Valid @RequestBody PlaybackReportRequest request) {
        return ApiResponse.success(playbackService.reportResult(request));
    }
}
