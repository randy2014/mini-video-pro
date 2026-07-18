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
public class EntitlementBatchVO {
    private Long id;
    private String batchNo;
    private Long productId;
    private String channelCode;
    private Integer quantity;
    private Integer generatedCount;
    private Integer activatedCount;
    private String status;
    private LocalDateTime createdAt;
}
