package com.video.entitlement.module.risk.repository;

import com.video.entitlement.module.risk.entity.RiskEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RiskEventRepository extends JpaRepository<RiskEvent, Long> {

    Page<RiskEvent> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Transactional void deleteByUserId(Long userId);
}
