package com.video.entitlement.module.risk.repository;

import com.video.entitlement.module.risk.entity.RiskBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskBlacklistRepository extends JpaRepository<RiskBlacklist, Long> {

    Optional<RiskBlacklist> findByBlacklistTypeAndTargetValue(String blacklistType, String targetValue);
}
