package com.video.entitlement.module.device.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum DeviceStatus implements CodeEnum {
    ACTIVE("ACTIVE", "正常绑定"),
    UNBOUND("UNBOUND", "已解绑"),
    BLOCKED("BLOCKED", "已封禁");

    private final String code;
    private final String description;

    DeviceStatus(String code, String description) {
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
