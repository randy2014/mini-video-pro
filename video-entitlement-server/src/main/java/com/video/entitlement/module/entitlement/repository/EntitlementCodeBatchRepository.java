package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.EntitlementCodeBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntitlementCodeBatchRepository extends JpaRepository<EntitlementCodeBatch, Long> {

    Optional<EntitlementCodeBatch> findByBatchNo(String batchNo);
}
