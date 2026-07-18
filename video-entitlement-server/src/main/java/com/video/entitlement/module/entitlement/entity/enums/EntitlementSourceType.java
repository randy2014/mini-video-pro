package com.video.entitlement.module.entitlement.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum EntitlementSourceType implements CodeEnum {
    CODE("CODE", "权益码兑换"),
    ADMIN_GRANT("ADMIN_GRANT", "管理员赠送"),
    ACTIVITY("ACTIVITY", "活动赠送"),
    CHANNEL("CHANNEL", "渠道发放");

    private final String code;
    private final String description;

    EntitlementSourceType(String code, String description) {
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
