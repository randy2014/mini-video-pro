package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.UserEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserEntitlementRepository extends JpaRepository<UserEntitlement, Long> {
    List<UserEntitlement> findByUserId(Long userId);
    Optional<UserEntitlement> findByUserIdAndEntitlementId(Long userId, Long entitlementId);
}
