package com.video.entitlement.module.health.service;

import com.video.entitlement.module.health.entity.RouteHealth;
import com.video.entitlement.module.health.entity.RouteMetricMinute;
import com.video.entitlement.module.health.entity.enums.CircuitState;
import com.video.entitlement.module.health.entity.enums.RouteHealthStatus;
import com.video.entitlement.module.health.repository.RouteHealthRepository;
import com.video.entitlement.module.health.repository.RouteMetricMinuteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final RouteHealthRepository healthRepo;
    private final RouteMetricMinuteRepository metricRepo;

    @Transactional
    public void recordSuccess(Long routeId, int durationMs) {
        RouteHealth health = healthRepo.findByRouteId(routeId)
                .orElseGet(() -> createDefault(routeId));
        health.setConsecutiveFailureCount(0);
        healthRepo.save(health);
        recordMetric(routeId, true);
    }

    @Transactional
    public void recordFailure(Long routeId) {
        RouteHealth health = healthRepo.findByRouteId(routeId)
                .orElseGet(() -> createDefault(routeId));
        int consecutiveFailures = health.getConsecutiveFailureCount() + 1;
        health.setConsecutiveFailureCount(consecutiveFailures);

        if (consecutiveFailures >= 5) {
            health.setHealthStatus(RouteHealthStatus.UNHEALTHY.getCode());
            health.setCircuitOpenUntil(LocalDateTime.now().plusMinutes(5));
        } else if (consecutiveFailures >= 3) {
            health.setHealthStatus(RouteHealthStatus.DEGRADED.getCode());
        }
        healthRepo.save(health);
        recordMetric(routeId, false);
    }

    private RouteHealth createDefault(Long routeId) {
        return healthRepo.save(RouteHealth.builder()
                .routeId(routeId).healthStatus(RouteHealthStatus.HEALTHY.getCode())
                .successRate5m(BigDecimal.ONE).consecutiveFailureCount(0).build());
    }

    private void recordMetric(Long routeId, boolean success) {
        LocalDateTime minute = LocalDateTime.now().withSecond(0).withNano(0);
        metricRepo.save(RouteMetricMinute.builder()
                .routeId(routeId).metricMinute(minute)
                .requestCount(1).successCount(success ? 1 : 0)
                .failureCount(success ? 0 : 1).build());
    }
}
