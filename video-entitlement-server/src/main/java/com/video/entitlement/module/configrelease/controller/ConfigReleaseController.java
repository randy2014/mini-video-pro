package com.video.entitlement.module.configrelease.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.response.PageResult;
import com.video.entitlement.module.configrelease.dto.*;
import com.video.entitlement.module.configrelease.entity.*;
import com.video.entitlement.module.configrelease.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "配置发布")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/admin/api/v1/config")
@RequiredArgsConstructor
public class ConfigReleaseController {
    private final ConfigReleaseRepository releaseRepo;
    private final ConfigReleaseItemRepository itemRepo;

    @Operation(summary = "发布列表")
    @GetMapping("/releases")
    public ApiResponse<PageResult<ConfigRelease>> listReleases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ConfigRelease> p = releaseRepo.findAll(PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        return ApiResponse.success(PageResult.of(p.getContent(), p.getTotalElements(), page, size));
    }

    @Operation(summary = "创建发布")
    @PostMapping("/releases")
    public ApiResponse<ConfigReleaseVO> createRelease(@Valid @RequestBody ConfigPublishRequest request) {
        ConfigRelease r = ConfigRelease.builder()
                .releaseNo("R" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .releaseType(request.getReleaseType()).configVersion("v" + System.currentTimeMillis())
                .status("DRAFT").grayPercentage(request.getGrayPercentage())
                .description(request.getDescription()).build();
        r = releaseRepo.save(r);
        return ApiResponse.success(ConfigReleaseVO.builder()
                .id(r.getId()).releaseNo(r.getReleaseNo()).releaseType(r.getReleaseType())
                .configVersion(r.getConfigVersion()).status(r.getStatus())
                .grayPercentage(r.getGrayPercentage()).description(r.getDescription())
                .createdAt(r.getCreatedAt()).build());
    }
}
