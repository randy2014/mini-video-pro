package com.video.entitlement.module.entitlement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitlementVO {
    private Long id;
    private String entitlementName;
    private String entitlementCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String ownerName;
    private String ownerPhone;
    private String ownerProfession;
    private LocalDateTime createdAt;
}
