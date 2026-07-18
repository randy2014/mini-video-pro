package com.video.entitlement.module.platform.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.platform.dto.*;
import com.video.entitlement.module.platform.entity.*;
import com.video.entitlement.module.platform.repository.*;
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
    private final VideoPlatformRepository platformRepo;
    private final VideoPlatformDomainRepository domainRepo;
    private final VideoUrlRuleRepository ruleRepo;

    @Operation(summary = "创建平台")
    @PostMapping
    public ApiResponse<VideoPlatformVO> create(@Valid @RequestBody PlatformConfigRequest request) {
        return ApiResponse.success(platformService.createPlatform(request));
    }

    @Operation(summary = "平台列表")
    @GetMapping
    public ApiResponse<List<VideoPlatform>> list() {
        return ApiResponse.success(platformRepo.findAll());
    }

    @Operation(summary = "添加域名")
    @PostMapping("/{platformId}/domains")
    public ApiResponse<VideoPlatformDomain> addDomain(@PathVariable Long platformId,
                                                       @RequestParam String host) {
        VideoPlatformDomain d = VideoPlatformDomain.builder()
                .platformId(platformId).host(host).scheme("https").enabled(true).build();
        return ApiResponse.success(domainRepo.save(d));
    }

    @Operation(summary = "添加URL规则")
    @PostMapping("/{platformId}/rules")
    public ApiResponse<VideoUrlRule> addRule(@PathVariable Long platformId,
                                              @RequestParam String ruleType,
                                              @RequestParam String pattern,
                                              @RequestParam(defaultValue = "0") int priority) {
        VideoUrlRule r = VideoUrlRule.builder()
                .platformId(platformId).ruleType(ruleType).pattern(pattern)
                .priority(priority).enabled(true).build();
        return ApiResponse.success(ruleRepo.save(r));
    }
}
