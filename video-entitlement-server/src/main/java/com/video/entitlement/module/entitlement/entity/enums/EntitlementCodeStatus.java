package com.video.entitlement.module.entitlement.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum EntitlementCodeStatus implements CodeEnum {
    UNUSED("UNUSED", "未使用"),
    ACTIVATED("ACTIVATED", "已激活"),
    EXPIRED("EXPIRED", "已过期"),
    DISABLED("DISABLED", "已停用"),
    CANCELLED("CANCELLED", "已作废");

    private final String code;
    private final String description;

    EntitlementCodeStatus(String code, String description) {
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
