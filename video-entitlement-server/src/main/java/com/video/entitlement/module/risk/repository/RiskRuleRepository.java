package com.video.entitlement.module.risk.repository;

import com.video.entitlement.module.risk.entity.RiskRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskRuleRepository extends JpaRepository<RiskRule, Long> {

    Optional<RiskRule> findByRuleCode(String code);

    List<RiskRule> findByEventTypeAndEnabled(String eventType, Boolean enabled);
}
