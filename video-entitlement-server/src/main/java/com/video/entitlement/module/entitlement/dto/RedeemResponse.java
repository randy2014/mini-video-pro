package com.video.entitlement.module.entitlement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemResponse {
    private UserEntitlementVO userEntitlement;
}
