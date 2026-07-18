package com.video.entitlement.module.user.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.user.dto.*;
import com.video.entitlement.module.user.service.CaptchaService;
import com.video.entitlement.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CaptchaService captchaService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public ApiResponse<CaptchaVO> captcha() {
        return ApiResponse.success(captchaService.generate());
    }

    @Operation(summary = "用户登录（账号+密码+权益码+图形码；账号不存在则自动注册）")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(userService.refresh(request));
    }
}
