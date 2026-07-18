package com.video.entitlement.module.entitlement.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.security.JwtUserPrincipal;
import com.video.entitlement.module.entitlement.dto.*;
import com.video.entitlement.module.entitlement.entity.UserEntitlement;
import com.video.entitlement.module.entitlement.repository.UserEntitlementRepository;
import com.video.entitlement.module.entitlement.service.EntitlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "权益")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/api/v1/entitlement")
@RequiredArgsConstructor
public class EntitlementController {
    private final EntitlementService entitlementService;
    private final UserEntitlementRepository userEntitlementRepo;

    @Operation(summary = "兑换权益码")
    @PostMapping("/redeem")
    public ApiResponse<RedeemResponse> redeem(@Valid @RequestBody RedeemRequest request,
                                               @AuthenticationPrincipal JwtUserPrincipal principal) {
        return ApiResponse.success(entitlementService.redeem(principal.getUserId(), request));
    }

    @Operation(summary = "我的权益列表")
    @GetMapping("/my")
    public ApiResponse<List<UserEntitlementVO>> myEntitlements(
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        List<UserEntitlement> list = userEntitlementRepo.findByUserId(principal.getUserId());
        List<UserEntitlementVO> vos = list.stream().map(e -> UserEntitlementVO.builder()
                .id(e.getId()).productCode(null).productName(null)
                .status(e.getStatus()).sourceType(e.getSourceType())
                .effectiveAt(e.getEffectiveAt()).expiresAt(e.getExpiresAt())
                .usedTotal(e.getUsedTotal()).build()).collect(Collectors.toList());
        return ApiResponse.success(vos);
    }
}
