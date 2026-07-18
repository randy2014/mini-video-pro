package com.video.entitlement.module.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppVersionVO {
    private String versionName;
    private int versionCode;
    private String downloadUrl;
    private String releaseNotes;
    private boolean forceUpdate;
}
