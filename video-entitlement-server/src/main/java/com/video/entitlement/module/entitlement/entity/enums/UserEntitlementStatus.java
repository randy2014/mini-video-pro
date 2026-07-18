package com.video.entitlement.module.entitlement.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum UserEntitlementStatus implements CodeEnum {
    PENDING("PENDING", "待生效"),
    ACTIVE("ACTIVE", "生效中"),
    SUSPENDED("SUSPENDED", "已暂停"),
    EXPIRED("EXPIRED", "已过期"),
    REVOKED("REVOKED", "已撤销");

    private final String code;
    private final String description;

    UserEntitlementStatus(String code, String description) {
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
