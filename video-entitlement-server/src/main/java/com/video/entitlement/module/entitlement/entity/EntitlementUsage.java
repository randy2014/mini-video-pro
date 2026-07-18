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
@Table(name = "entitlement_usage")
public class EntitlementUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_entitlement_id", nullable = false)
    private Long userEntitlementId;

    @Column(name = "platform_code", nullable = false, length = 64)
    private String platformCode;

    @Column(name = "content_key", length = 255)
    private String contentKey;

    @Column(name = "usage_date", nullable = false)
    private LocalDateTime usageDate;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
