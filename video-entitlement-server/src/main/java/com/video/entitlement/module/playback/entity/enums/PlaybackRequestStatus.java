package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum PlaybackRequestStatus implements CodeEnum {
    CREATED("CREATED", "请求已创建"),
    RESOLVED("RESOLVED", "已生成决策"),
    PLAYING("PLAYING", "播放阶段"),
    SUCCESS("SUCCESS", "播放成功"),
    FAILED("FAILED", "播放失败"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    PlaybackRequestStatus(String code, String description) {
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
