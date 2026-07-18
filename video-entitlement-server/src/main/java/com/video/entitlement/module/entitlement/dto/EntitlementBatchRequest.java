package com.video.entitlement.module.entitlement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitlementBatchRequest {
    @NotNull
    private Long productId;

    private String channelCode;

    @NotNull
    private Integer quantity;
}
