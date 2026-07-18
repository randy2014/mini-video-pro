package com.video.entitlement.module.entitlement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitlementProductVO {
    private Long id;
    private String productCode;
    private String productName;
    private String description;
    private String validityType;
    private Integer validDays;
    private Integer dailyUsageLimit;
    private Integer totalUsageLimit;
    private Integer deviceLimit;
    private String status;
}
