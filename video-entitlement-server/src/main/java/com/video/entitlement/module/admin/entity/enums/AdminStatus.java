package com.video.entitlement.module.admin.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum AdminStatus implements CodeEnum {
    ACTIVE("ACTIVE", "正常"),
    DISABLED("DISABLED", "已禁用"),
    LOCKED("LOCKED", "已锁定");

    private final String code;
    private final String description;

    AdminStatus(String code, String description) {
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
