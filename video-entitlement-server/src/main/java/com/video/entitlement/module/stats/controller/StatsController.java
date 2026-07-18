package com.video.entitlement.module.stats.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.platform.repository.VideoPlatformRepository;
import com.video.entitlement.module.user.repository.UserAccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "统计")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/admin/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {
    private final UserAccountRepository userRepo;
    private final VideoPlatformRepository platformRepo;

    @Operation(summary = "核心统计")
    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        return ApiResponse.success(Map.of(
                "totalUsers", userRepo.count(),
                "totalPlatforms", platformRepo.count()
        ));
    }
}
