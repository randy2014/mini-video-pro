package com.video.entitlement.module.entitlement.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.response.PageResult;
import com.video.entitlement.module.entitlement.dto.*;
import com.video.entitlement.module.entitlement.entity.*;
import com.video.entitlement.module.entitlement.repository.*;
import com.video.entitlement.module.entitlement.service.EntitlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权益管理")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/admin/api/v1/entitlement")
@RequiredArgsConstructor
public class AdminEntitlementController {
    private final EntitlementService entitlementService;
    private final EntitlementProductRepository productRepo;
    private final EntitlementCodeBatchRepository batchRepo;

    @Operation(summary = "创建权益产品")
    @PostMapping("/products")
    public ApiResponse<EntitlementProductVO> createProduct(@Valid @RequestBody EntitlementProductVO vo) {
        return ApiResponse.success(entitlementService.createProduct(vo));
    }

    @Operation(summary = "权益产品列表")
    @GetMapping("/products")
    public ApiResponse<PageResult<EntitlementProduct>> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EntitlementProduct> p = productRepo.findAll(PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        return ApiResponse.success(PageResult.of(p.getContent(), p.getTotalElements(), page, size));
    }

    @Operation(summary = "生成权益码批次")
    @PostMapping("/batches")
    public ApiResponse<EntitlementBatchVO> createBatch(@Valid @RequestBody EntitlementBatchRequest request) {
        return ApiResponse.success(entitlementService.createBatch(request));
    }

    @Operation(summary = "批次列表")
    @GetMapping("/batches")
    public ApiResponse<PageResult<EntitlementCodeBatch>> listBatches(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EntitlementCodeBatch> p = batchRepo.findAll(PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        return ApiResponse.success(PageResult.of(p.getContent(), p.getTotalElements(), page, size));
    }
}
