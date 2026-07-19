package com.video.entitlement.module.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppVersionRequest {
    private String versionName;
    private Integer versionCode;
    private String downloadUrl;
    private String releaseNotes;
    private Boolean forceUpdate;
}
