package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.Entitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EntitlementRepository extends JpaRepository<Entitlement, Long> {
    Optional<Entitlement> findByEntitlementCode(String code);
}
