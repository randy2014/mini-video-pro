package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.EntitlementCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface EntitlementCodeRepository extends JpaRepository<EntitlementCode, Long> {

    Optional<EntitlementCode> findByCodeHash(String codeHash);

    long countByBatchIdAndStatus(Long batchId, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EntitlementCode e WHERE e.codeHash = :codeHash")
    Optional<EntitlementCode> findByCodeHashForUpdate(@Param("codeHash") String codeHash);
}
