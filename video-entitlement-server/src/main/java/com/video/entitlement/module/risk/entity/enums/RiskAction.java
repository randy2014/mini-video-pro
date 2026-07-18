package com.video.entitlement.module.risk.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum RiskAction implements CodeEnum {
    LOG("LOG", "记录日志"),
    BLOCK("BLOCK", "阻止"),
    BAN("BAN", "封禁"),
    LIMIT("LIMIT", "限流");

    private final String code;
    private final String description;

    RiskAction(String code, String description) {
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
