package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum PlaybackErrorType implements CodeEnum {
    PAGE_LOAD_FAILED("PAGE_LOAD_FAILED", "页面加载失败"),
    TIMEOUT("TIMEOUT", "加载或播放超时");

    private final String code;
    private final String description;

    PlaybackErrorType(String code, String description) {
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
