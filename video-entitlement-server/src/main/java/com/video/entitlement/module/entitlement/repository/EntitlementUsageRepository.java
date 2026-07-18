package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.EntitlementUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EntitlementUsageRepository extends JpaRepository<EntitlementUsage, Long> {

    Optional<EntitlementUsage> findByUserEntitlementIdAndUsageDate(Long userEntitlementId, LocalDate usageDate);
}
