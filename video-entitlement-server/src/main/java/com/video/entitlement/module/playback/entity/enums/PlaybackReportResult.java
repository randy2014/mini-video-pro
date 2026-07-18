package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum PlaybackReportResult implements CodeEnum {
    SUCCESS("SUCCESS", "播放成功"),
    FAILED("FAILED", "播放失败"),
    CANCELLED("CANCELLED", "用户取消");

    private final String code;
    private final String description;

    PlaybackReportResult(String code, String description) {
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
