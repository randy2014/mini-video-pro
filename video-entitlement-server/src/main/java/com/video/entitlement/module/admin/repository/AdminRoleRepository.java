package com.video.entitlement.module.admin.repository;

import com.video.entitlement.module.admin.entity.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {

    Optional<AdminRole> findByRoleCode(String roleCode);
}
