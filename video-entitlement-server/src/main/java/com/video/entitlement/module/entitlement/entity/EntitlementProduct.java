package com.video.entitlement.module.entitlement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "entitlement_product")
public class EntitlementProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true, length = 64)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 128)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "validity_type", length = 32)
    private String validityType;

    @Column(name = "valid_days")
    private Integer validDays;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "daily_usage_limit")
    private Integer dailyUsageLimit;

    @Column(name = "total_usage_limit")
    private Integer totalUsageLimit;

    @Column(name = "device_limit")
    private Integer deviceLimit;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "DRAFT";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
