package com.video.entitlement.module.platform.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.platform.dto.*;
import com.video.entitlement.module.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "平台配置")
@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class PlatformController {
    private final PlatformService platformService;

    @Operation(summary = "获取可用平台列表")
    @GetMapping("/platforms")
    public ApiResponse<List<VideoPlatformVO>> getPlatforms() {
        return ApiResponse.success(platformService.getActivePlatforms());
    }

    @Operation(summary = "URL标准化和识别")
    @SecurityRequirement(name = "Bearer")
    @PostMapping("/url/standardize")
    public ApiResponse<UrlStandardizeResponse> standardizeUrl(@Valid @RequestBody UrlStandardizeRequest request) {
        return ApiResponse.success(platformService.standardizeUrl(request));
    }
}
