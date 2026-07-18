package com.video.entitlement.module.risk.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum RiskEventType implements CodeEnum {
    LOGIN_FAILED("LOGIN_FAILED", "登录失败"),
    HIGH_FREQUENCY_REQUEST("HIGH_FREQUENCY_REQUEST", "高频请求"),
    CODE_REDEEM_ABNORMAL("CODE_REDEEM_ABNORMAL", "异常兑换"),
    PLAYBACK_ABNORMAL("PLAYBACK_ABNORMAL", "异常播放");

    private final String code;
    private final String description;

    RiskEventType(String code, String description) {
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
