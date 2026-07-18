package com.video.entitlement.module.health.repository;

import com.video.entitlement.module.health.entity.RouteMetricMinute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteMetricMinuteRepository extends JpaRepository<RouteMetricMinute, Long> {

    List<RouteMetricMinute> findByRouteIdAndMetricMinuteBetween(Long routeId, LocalDateTime start, LocalDateTime end);
}
