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
public class UserEntitlementVO {
    private Long id;
    private String productCode;
    private String productName;
    private String status;
    private String sourceType;
    private LocalDateTime effectiveAt;
    private LocalDateTime expiresAt;
    private Integer usedTotal;
}
