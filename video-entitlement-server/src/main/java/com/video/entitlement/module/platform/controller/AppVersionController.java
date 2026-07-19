package com.video.entitlement.module.platform.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.platform.dto.AppVersionRequest;
import com.video.entitlement.module.platform.dto.AppVersionVO;
import com.video.entitlement.module.platform.service.AppVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "APP版本管理")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppVersionController {

    private final AppVersionService appVersionService;

    // ==================== 客户端接口 ====================

    @Operation(summary = "客户端检查APP版本更新")
    @GetMapping("/client/version")
    public ApiResponse<AppVersionVO> checkVersion() {
        AppVersionVO vo = appVersionService.getLatestVersion();
        return ApiResponse.success(vo);
    }

    // ==================== 管理端接口 ====================

    @Operation(summary = "版本列表")
    @GetMapping("/admin/versions")
    public ApiResponse<List<AppVersionVO>> list() {
        return ApiResponse.success(appVersionService.listAll());
    }

    @Operation(summary = "发布新版本")
    @PostMapping("/admin/versions")
    public ApiResponse<AppVersionVO> create(@RequestBody AppVersionRequest req) {
        return ApiResponse.success(appVersionService.create(req));
    }

    @Operation(summary = "更新版本信息")
    @PutMapping("/admin/versions/{id}")
    public ApiResponse<AppVersionVO> update(@PathVariable Long id, @RequestBody AppVersionRequest req) {
        return ApiResponse.success(appVersionService.update(id, req));
    }

    @Operation(summary = "切换版本状态 (ACTIVE/INACTIVE)")
    @PutMapping("/admin/versions/{id}/status")
    public ApiResponse<AppVersionVO> toggleStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.success(appVersionService.toggleStatus(id, status));
    }

    @Operation(summary = "上传 APK 发布新版本（文件可选，不传则使用下载地址）")
    @PostMapping(value = "/admin/versions/upload", consumes = "multipart/form-data")
    public ApiResponse<AppVersionVO> upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @ModelAttribute AppVersionRequest req) {
        return ApiResponse.success(appVersionService.uploadVersion(file, req));
    }

    @Operation(summary = "替换已有版本的 APK 文件")
    @PostMapping(value = "/admin/versions/{id}/upload", consumes = "multipart/form-data")
    public ApiResponse<AppVersionVO> replaceApk(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(appVersionService.replaceApk(id, file));
    }

    @Operation(summary = "删除版本（同时删除服务器上的 APK 文件）")
    @DeleteMapping("/admin/versions/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        appVersionService.delete(id);
        return ApiResponse.success(null);
    }
}
