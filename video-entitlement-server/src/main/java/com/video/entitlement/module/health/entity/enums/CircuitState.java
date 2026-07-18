package com.video.entitlement.module.health.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum CircuitState implements CodeEnum {
    CLOSED("CLOSED", "关闭"),
    OPEN("OPEN", "打开"),
    HALF_OPEN("HALF_OPEN", "半开");

    private final String code;
    private final String description;

    CircuitState(String code, String description) {
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
