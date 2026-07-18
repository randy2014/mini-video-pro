package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum RouteSelectionStrategy implements CodeEnum {
    PRIORITY("PRIORITY", "按优先级选择"),
    WEIGHTED("WEIGHTED", "按权重随机"),
    ROUND_ROBIN("ROUND_ROBIN", "轮询选择"),
    SUCCESS_RATE("SUCCESS_RATE", "按成功率选择");

    private final String code;
    private final String description;

    RouteSelectionStrategy(String code, String description) {
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
