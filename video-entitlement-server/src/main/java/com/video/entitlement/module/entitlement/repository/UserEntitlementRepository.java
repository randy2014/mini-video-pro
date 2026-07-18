package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.UserEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEntitlementRepository extends JpaRepository<UserEntitlement, Long> {

    List<UserEntitlement> findByUserIdAndStatus(Long userId, String status);

    List<UserEntitlement> findByUserId(Long userId);
}
