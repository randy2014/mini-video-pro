package com.video.entitlement.common.util;

import com.video.entitlement.common.enums.CodeEnum;
import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public final class EnumConverter {

    private EnumConverter() {}

    public static <E extends Enum<E> & CodeEnum> E fromCode(Class<E> enumClass, String code) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_ENUM_VALUE,
                        "Invalid code '" + code + "' for " + enumClass.getSimpleName()));
    }

    public static <E extends Enum<E> & CodeEnum> E fromCodeOrNull(Class<E> enumClass, String code) {
        if (code == null) return null;
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
