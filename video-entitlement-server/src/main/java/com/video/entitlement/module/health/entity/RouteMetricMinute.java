package com.video.entitlement.module.health.entity;

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
@Table(name = "route_metric_minute")
public class RouteMetricMinute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(name = "metric_minute", nullable = false)
    private LocalDateTime metricMinute;

    @Column(name = "request_count", nullable = false)
    @Builder.Default
    private Integer requestCount = 0;

    @Column(name = "success_count", nullable = false)
    @Builder.Default
    private Integer successCount = 0;

    @Column(name = "failure_count", nullable = false)
    @Builder.Default
    private Integer failureCount = 0;

    @Column(name = "p95_ms")
    private Long p95Ms;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
