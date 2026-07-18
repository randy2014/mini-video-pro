package com.video.entitlement.module.entitlement.repository;

import com.video.entitlement.module.entitlement.entity.EntitlementProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntitlementProductRepository extends JpaRepository<EntitlementProduct, Long> {

    Optional<EntitlementProduct> findByProductCode(String code);
}
