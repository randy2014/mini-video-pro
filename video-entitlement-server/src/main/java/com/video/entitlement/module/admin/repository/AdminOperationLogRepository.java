package com.video.entitlement.module.admin.repository;

import com.video.entitlement.module.admin.entity.AdminOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOperationLogRepository extends JpaRepository<AdminOperationLog, Long> {

    Page<AdminOperationLog> findByAdminUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
