package com.video.entitlement.module.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlStandardizeResponse {
    private String canonicalUrl;
    private String contentKey;
    private String platformCode;
    private Boolean matched;
}
