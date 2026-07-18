package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.EntitlementProductPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntitlementProductPlatformRepository extends JpaRepository<EntitlementProductPlatform, Long> {

    List<EntitlementProductPlatform> findByProductId(Long productId);
}
