package com.video.entitlement.module.health.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum RouteHealthStatus implements CodeEnum {
    HEALTHY("HEALTHY", "健康"),
    DEGRADED("DEGRADED", "降级"),
    UNHEALTHY("UNHEALTHY", "不健康"),
    DISABLED("DISABLED", "已禁用");

    private final String code;
    private final String description;

    RouteHealthStatus(String code, String description) {
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
