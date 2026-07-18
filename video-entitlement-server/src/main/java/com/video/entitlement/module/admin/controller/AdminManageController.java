package com.video.entitlement.module.admin.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.response.PageResult;
import com.video.entitlement.module.admin.dto.AdminVO;
import com.video.entitlement.module.admin.entity.AdminOperationLog;
import com.video.entitlement.module.admin.entity.AdminRole;
import com.video.entitlement.module.admin.entity.AdminUser;
import com.video.entitlement.module.admin.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "管理员管理")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/admin/api/v1/admin")
@RequiredArgsConstructor
public class AdminManageController {
    private final AdminUserRepository adminUserRepo;
    private final AdminOperationLogRepository operationLogRepo;
    private final AdminRoleRepository roleRepo;

    @Operation(summary = "管理员列表")
    @GetMapping("/users")
    public ApiResponse<PageResult<AdminVO>> listUsers(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        Page<AdminUser> p = adminUserRepo.findAll(PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        List<AdminVO> vos = p.getContent().stream().map(u -> AdminVO.builder()
                .id(u.getId()).username(u.getUsername()).status(u.getStatus())
                .lastLoginAt(u.getLastLoginAt()).createdAt(u.getCreatedAt()).build()).toList();
        return ApiResponse.success(PageResult.of(vos, p.getTotalElements(), page, size));
    }

    @Operation(summary = "操作日志")
    @GetMapping("/operation-logs")
    public ApiResponse<PageResult<AdminOperationLog>> operationLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId) {
        Page<AdminOperationLog> p;
        if (userId != null) {
            p = operationLogRepo.findByAdminUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page - 1, size));
        } else {
            p = operationLogRepo.findAll(PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        }
        return ApiResponse.success(PageResult.of(p.getContent(), p.getTotalElements(), page, size));
    }

    @Operation(summary = "角色列表")
    @GetMapping("/roles")
    public ApiResponse<List<AdminRole>> listRoles() {
        return ApiResponse.success(roleRepo.findAll());
    }
}
