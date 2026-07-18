package com.video.entitlement.module.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVO {
    private Long id;
    private String username;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
