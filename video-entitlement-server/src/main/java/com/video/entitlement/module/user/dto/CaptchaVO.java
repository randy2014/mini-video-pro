package com.video.entitlement.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {
    /** 图形码标识，登录时需回传用于校验 */
    private String captchaKey;
    /** base64 编码的 PNG 图片，形如 data:image/png;base64,xxxx */
    private String imageBase64;
}
