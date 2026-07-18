package com.video.entitlement.module.entitlement.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum EntitlementProductStatus implements CodeEnum {
    DRAFT("DRAFT", "草稿"),
    ACTIVE("ACTIVE", "已启用"),
    DISABLED("DISABLED", "已停用"),
    EXPIRED("EXPIRED", "已过期");

    private final String code;
    private final String description;

    EntitlementProductStatus(String code, String description) {
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
