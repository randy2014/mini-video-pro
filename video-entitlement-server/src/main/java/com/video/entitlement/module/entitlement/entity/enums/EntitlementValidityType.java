package com.video.entitlement.module.entitlement.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum EntitlementValidityType implements CodeEnum {
    AFTER_ACTIVATION("AFTER_ACTIVATION", "激活后生效"),
    FIXED_PERIOD("FIXED_PERIOD", "固定时间段"),
    PERMANENT("PERMANENT", "永久有效");

    private final String code;
    private final String description;

    EntitlementValidityType(String code, String description) {
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
