package com.video.entitlement.module.health.repository;

import com.video.entitlement.module.health.entity.RouteHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteHealthRepository extends JpaRepository<RouteHealth, Long> {

    Optional<RouteHealth> findByRouteId(Long routeId);
}
