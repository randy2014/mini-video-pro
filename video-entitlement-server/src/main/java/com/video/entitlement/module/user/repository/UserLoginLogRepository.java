package com.video.entitlement.module.user.repository;

import com.video.entitlement.module.user.entity.UserLoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {

    Page<UserLoginLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
