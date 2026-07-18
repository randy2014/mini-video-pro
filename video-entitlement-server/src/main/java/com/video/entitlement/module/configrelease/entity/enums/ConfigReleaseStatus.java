package com.video.entitlement.module.configrelease.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum ConfigReleaseStatus implements CodeEnum {
    DRAFT("DRAFT", "草稿"),
    PENDING("PENDING", "待发布"),
    PUBLISHED("PUBLISHED", "已发布"),
    CANCELLED("CANCELLED", "已取消"),
    ROLLED_BACK("ROLLED_BACK", "已回滚");

    private final String code;
    private final String description;

    ConfigReleaseStatus(String code, String description) {
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
