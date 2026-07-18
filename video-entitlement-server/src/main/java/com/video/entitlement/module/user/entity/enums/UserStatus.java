package com.video.entitlement.module.user.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum UserStatus implements CodeEnum {
    ACTIVE("ACTIVE", "正常"),
    DISABLED("DISABLED", "已禁用"),
    CANCELLED("CANCELLED", "已注销"),
    RISK_BLOCKED("RISK_BLOCKED", "风控封禁");

    private final String code;
    private final String description;

    UserStatus(String code, String description) {
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
