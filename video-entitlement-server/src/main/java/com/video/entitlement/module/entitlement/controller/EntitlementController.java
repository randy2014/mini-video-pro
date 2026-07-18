package com.video.entitlement.module.entitlement.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.response.PageResult;
import com.video.entitlement.module.entitlement.dto.EntitlementRequest;
import com.video.entitlement.module.entitlement.dto.EntitlementVO;
import com.video.entitlement.module.entitlement.service.EntitlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "权益管理")
@RestController
@RequestMapping("/admin/api/v1/entitlements")
@RequiredArgsConstructor
public class EntitlementController {

    private final EntitlementService entitlementService;

    @Operation(summary = "权益列表")
    @GetMapping
    public ApiResponse<PageResult<EntitlementVO>> list(Pageable pageable) {
        Page<EntitlementVO> page = entitlementService.list(pageable);
        return ApiResponse.success(PageResult.of(
                page.getContent(), page.getTotalElements(),
                page.getNumber() + 1, page.getSize()));
    }

    @Operation(summary = "权益详情")
    @GetMapping("/{id}")
    public ApiResponse<EntitlementVO> get(@PathVariable Long id) {
        return ApiResponse.success(entitlementService.get(id));
    }

    @Operation(summary = "新增权益")
    @PostMapping
    public ApiResponse<EntitlementVO> create(@Valid @RequestBody EntitlementRequest req) {
        return ApiResponse.success(entitlementService.create(req));
    }

    @Operation(summary = "编辑权益")
    @PutMapping("/{id}")
    public ApiResponse<EntitlementVO> update(@PathVariable Long id, @Valid @RequestBody EntitlementRequest req) {
        return ApiResponse.success(entitlementService.update(id, req));
    }

    @Operation(summary = "删除权益")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        entitlementService.delete(id);
        return ApiResponse.success(null);
    }
}
