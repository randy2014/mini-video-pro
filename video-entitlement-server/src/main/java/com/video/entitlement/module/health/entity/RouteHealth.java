package com.video.entitlement.module.health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "route_health")
public class RouteHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_id", nullable = false, unique = true)
    private Long routeId;

    @Column(name = "health_status", nullable = false, length = 32)
    @Builder.Default
    private String healthStatus = "HEALTHY";

    @Column(name = "success_rate_5m", nullable = false, precision = 4, scale = 4)
    @Builder.Default
    private BigDecimal successRate5m = new BigDecimal("1.0000");

    @Column(name = "consecutive_failure_count", nullable = false)
    @Builder.Default
    private Integer consecutiveFailureCount = 0;

    @Column(name = "circuit_open_until")
    private LocalDateTime circuitOpenUntil;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
