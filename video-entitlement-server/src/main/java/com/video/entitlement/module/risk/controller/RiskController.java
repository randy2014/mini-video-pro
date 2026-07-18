package com.video.entitlement.module.risk.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.response.PageResult;
import com.video.entitlement.module.risk.dto.RiskEventVO;
import com.video.entitlement.module.risk.entity.*;
import com.video.entitlement.module.risk.repository.*;
import com.video.entitlement.module.risk.service.RiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "风控管理")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/admin/api/v1/risk")
@RequiredArgsConstructor
public class RiskController {
    private final RiskService riskService;
    private final RiskBlacklistRepository blacklistRepo;
    private final RiskEventRepository eventRepo;
    private final RiskRuleRepository ruleRepo;

    @Operation(summary = "黑名单列表")
    @GetMapping("/blacklist")
    public ApiResponse<List<RiskBlacklist>> listBlacklist() {
        return ApiResponse.success(blacklistRepo.findAll());
    }

    @Operation(summary = "添加黑名单")
    @PostMapping("/blacklist")
    public ApiResponse<RiskBlacklist> addBlacklist(@RequestParam String type,
                                                    @RequestParam String value,
                                                    @RequestParam String reason) {
        RiskBlacklist b = RiskBlacklist.builder()
                .blacklistType(type).targetValue(value).reason(reason)
                .status("ACTIVE").startTime(java.time.LocalDateTime.now()).build();
        return ApiResponse.success(blacklistRepo.save(b));
    }

    @Operation(summary = "风险事件列表")
    @GetMapping("/events")
    public ApiResponse<PageResult<RiskEvent>> listEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RiskEvent> p = eventRepo.findAll(PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        return ApiResponse.success(PageResult.of(p.getContent(), p.getTotalElements(), page, size));
    }

    @Operation(summary = "风控规则列表")
    @GetMapping("/rules")
    public ApiResponse<List<RiskRule>> listRules() {
        return ApiResponse.success(ruleRepo.findAll());
    }
}
