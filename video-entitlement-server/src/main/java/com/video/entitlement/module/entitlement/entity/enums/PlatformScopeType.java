package com.video.entitlement.module.entitlement.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum PlatformScopeType implements CodeEnum {
    ALL("ALL", "全部平台"),
    INCLUDE("INCLUDE", "仅包含"),
    EXCLUDE("EXCLUDE", "排除");

    private final String code;
    private final String description;

    PlatformScopeType(String code, String description) {
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
