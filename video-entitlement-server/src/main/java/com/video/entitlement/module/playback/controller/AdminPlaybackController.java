package com.video.entitlement.module.playback.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.playback.dto.*;
import com.video.entitlement.module.playback.entity.*;
import com.video.entitlement.module.playback.repository.*;
import com.video.entitlement.module.health.entity.RouteHealth;
import com.video.entitlement.module.health.repository.RouteHealthRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "播放路由管理")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/admin/api/v1/playback")
@RequiredArgsConstructor
public class AdminPlaybackController {
    private final PlaybackProviderRepository providerRepo;
    private final PlaybackRouteGroupRepository groupRepo;
    private final PlaybackRouteRepository routeRepo;
    private final PlaybackRuleRepository ruleRepo;
    private final RouteHealthRepository healthRepo;

    @Operation(summary = "供应商列表")
    @GetMapping("/providers")
    public ApiResponse<List<PlaybackProvider>> listProviders() {
        return ApiResponse.success(providerRepo.findAll());
    }

    @Operation(summary = "创建供应商")
    @PostMapping("/providers")
    public ApiResponse<PlaybackProvider> createProvider(@Valid @RequestBody PlaybackProviderVO vo) {
        PlaybackProvider p = PlaybackProvider.builder()
                .providerCode(vo.getProviderCode()).providerName(vo.getProviderName())
                .providerType(vo.getProviderType()).status("PENDING")
                .authorizationStatus("PENDING").build();
        return ApiResponse.success(providerRepo.save(p));
    }

    @Operation(summary = "线路组列表")
    @GetMapping("/groups")
    public ApiResponse<List<PlaybackRouteGroup>> listGroups() {
        return ApiResponse.success(groupRepo.findAll());
    }

    @Operation(summary = "线路列表")
    @GetMapping("/routes")
    public ApiResponse<List<PlaybackRoute>> listRoutes(@RequestParam Long groupId) {
        return ApiResponse.success(routeRepo.findByGroupIdAndEnabledOrderByPriorityAsc(groupId, true));
    }

    @Operation(summary = "创建线路")
    @PostMapping("/routes")
    public ApiResponse<PlaybackRoute> createRoute(@Valid @RequestBody PlaybackRouteVO vo) {
        PlaybackRoute r = PlaybackRoute.builder()
                .routeCode(vo.getRouteCode()).providerId(1L) // TODO: resolve from providerCode
                .groupId(1L).routeType(vo.getRouteType())
                .targetTemplate(vo.getTargetTemplate()).priority(vo.getPriority())
                .enabled(vo.getEnabled()).authorizationStatus("PENDING").build();
        return ApiResponse.success(routeRepo.save(r));
    }

    @Operation(summary = "规则列表")
    @GetMapping("/rules")
    public ApiResponse<List<PlaybackRule>> listRules(@RequestParam String platformCode) {
        return ApiResponse.success(ruleRepo.findByPlatformCodeAndEnabledOrderByPriorityAsc(platformCode, true));
    }

    @Operation(summary = "创建规则")
    @PostMapping("/rules")
    public ApiResponse<PlaybackRule> createRule(@Valid @RequestBody PlaybackRuleVO vo) {
        PlaybackRule r = PlaybackRule.builder()
                .platformCode(vo.getPlatformCode()).clientType(vo.getClientType())
                .routeGroupId(vo.getRouteGroupId()).priority(vo.getPriority())
                .enabled(vo.getEnabled()).build();
        return ApiResponse.success(ruleRepo.save(r));
    }

    @Operation(summary = "线路健康状态")
    @GetMapping("/health")
    public ApiResponse<List<RouteHealth>> routeHealth() {
        return ApiResponse.success(healthRepo.findAll());
    }
}
