package com.video.entitlement.module.platform.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.platform.dto.AppVersionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "APP版本更新")
@RestController
@RequestMapping("/api/v1/client")
public class AppVersionController {

    @Value("${app.version.name:0.9.202607190000}")
    private String versionName;

    @Value("${app.version.code:7190000}")
    private int versionCode;

    @Value("${app.version.download-url:}")
    private String downloadUrl;

    @Value("${app.version.release-notes:}")
    private String releaseNotes;

    @Value("${app.version.force-update:false}")
    private boolean forceUpdate;

    @Operation(summary = "检查APP版本更新")
    @GetMapping("/version")
    public ApiResponse<AppVersionVO> checkVersion() {
        return ApiResponse.success(AppVersionVO.builder()
                .versionName(versionName)
                .versionCode(versionCode)
                .downloadUrl(downloadUrl)
                .releaseNotes(releaseNotes)
                .forceUpdate(forceUpdate)
                .build());
    }
}
