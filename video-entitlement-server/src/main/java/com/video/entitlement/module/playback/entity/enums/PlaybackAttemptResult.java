package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum PlaybackAttemptResult implements CodeEnum {
    PENDING("PENDING", "等待反馈"),
    SUCCESS("SUCCESS", "当前线路成功"),
    FAILED("FAILED", "当前线路失败"),
    CANCELLED("CANCELLED", "用户取消"),
    TIMEOUT("TIMEOUT", "反馈超时");

    private final String code;
    private final String description;

    PlaybackAttemptResult(String code, String description) {
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
