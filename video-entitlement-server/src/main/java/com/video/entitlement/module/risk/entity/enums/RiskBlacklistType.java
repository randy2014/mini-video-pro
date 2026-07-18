package com.video.entitlement.module.risk.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum RiskBlacklistType implements CodeEnum {
    USER("USER", "用户"),
    DEVICE("DEVICE", "设备"),
    IP("IP", "IP地址");

    private final String code;
    private final String description;

    RiskBlacklistType(String code, String description) {
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
