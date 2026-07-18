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

@Tag(name = "平台管理")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/admin/api/v1/platform")
@RequiredArgsConstructor
public class AdminPlatformController {
    private final PlatformService platformService;

    @Operation(summary = "平台列表")
    @GetMapping
    public ApiResponse<List<VideoPlatformVO>> list() {
        return ApiResponse.success(platformService.getAllPlatforms());
    }

    @Operation(summary = "创建平台")
    @PostMapping
    public ApiResponse<VideoPlatformVO> create(@Valid @RequestBody PlatformConfigRequest request) {
        return ApiResponse.success(platformService.createPlatform(request));
    }

    @Operation(summary = "编辑平台")
    @PutMapping("/{id}")
    public ApiResponse<VideoPlatformVO> update(@PathVariable Long id,
                                                @Valid @RequestBody PlatformConfigRequest request) {
        return ApiResponse.success(platformService.updatePlatform(id, request));
    }

    @Operation(summary = "删除平台")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        platformService.deletePlatform(id);
        return ApiResponse.success(null);
    }
}
