package com.video.entitlement.module.admin.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum RiskLevel implements CodeEnum {
    LOW("LOW", "低风险"),
    MEDIUM("MEDIUM", "中风险"),
    HIGH("HIGH", "高风险"),
    CRITICAL("CRITICAL", "严重风险");

    private final String code;
    private final String description;

    RiskLevel(String code, String description) {
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
