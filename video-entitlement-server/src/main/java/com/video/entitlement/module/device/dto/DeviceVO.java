package com.video.entitlement.module.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVO {
    private Long id;
    private String devicePublicId;
    private String clientType;
    private String status;
    private String appVersion;
    private LocalDateTime lastActiveAt;
}
