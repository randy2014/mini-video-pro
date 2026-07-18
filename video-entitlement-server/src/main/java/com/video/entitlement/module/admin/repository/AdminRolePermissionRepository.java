package com.video.entitlement.module.admin.repository;

import com.video.entitlement.module.admin.entity.AdminRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AdminRolePermissionRepository extends JpaRepository<AdminRolePermission, Long> {

    List<AdminRolePermission> findByRoleId(Long roleId);

    @Modifying
    @Transactional
    void deleteByRoleId(Long roleId);
}
