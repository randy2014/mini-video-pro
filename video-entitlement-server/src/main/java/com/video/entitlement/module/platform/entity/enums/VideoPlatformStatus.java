package com.video.entitlement.module.platform.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum VideoPlatformStatus implements CodeEnum {
    ACTIVE("ACTIVE", "正常启用"),
    MAINTENANCE("MAINTENANCE", "维护中"),
    DISABLED("DISABLED", "已停用");

    private final String code;
    private final String description;

    VideoPlatformStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
