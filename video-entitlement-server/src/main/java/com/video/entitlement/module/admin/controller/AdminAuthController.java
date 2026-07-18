package com.video.entitlement.module.admin.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.admin.dto.AdminCreateRequest;
import com.video.entitlement.module.admin.dto.AdminLoginRequest;
import com.video.entitlement.module.admin.dto.AdminLoginResponse;
import com.video.entitlement.module.admin.dto.AdminVO;
import com.video.entitlement.module.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员认证")
@RestController
@RequestMapping("/admin/api/v1/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminService adminService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(adminService.login(request));
    }

    @Operation(summary = "创建管理员")
    @SecurityRequirement(name = "Bearer")
    @PostMapping("/create-admin")
    public ApiResponse<AdminVO> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ApiResponse.success(adminService.createAdmin(request));
    }
}
