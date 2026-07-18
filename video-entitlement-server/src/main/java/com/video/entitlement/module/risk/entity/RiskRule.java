package com.video.entitlement.module.risk.entity;

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
@Table(name = "risk_rule")
public class RiskRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_code", nullable = false, unique = true, length = 64)
    private String ruleCode;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @Column(nullable = false)
    private Integer threshold;

    @Column(name = "window_seconds", nullable = false)
    private Integer windowSeconds;

    @Column(nullable = false, length = 32)
    private String action;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
