package com.video.entitlement.module.user.repository;

import com.video.entitlement.module.user.entity.InviteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InviteRecordRepository extends JpaRepository<InviteRecord, Long> {
    List<InviteRecord> findByInviterUserId(Long inviterUserId);
    long countByCode(String code);
}
