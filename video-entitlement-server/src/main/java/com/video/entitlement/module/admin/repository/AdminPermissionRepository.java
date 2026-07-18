package com.video.entitlement.module.admin.repository;

import com.video.entitlement.module.admin.entity.AdminPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminPermissionRepository extends JpaRepository<AdminPermission, Long> {

    Optional<AdminPermission> findByPermissionCode(String code);

    List<AdminPermission> findAllByPathIn(List<String> paths);
}
