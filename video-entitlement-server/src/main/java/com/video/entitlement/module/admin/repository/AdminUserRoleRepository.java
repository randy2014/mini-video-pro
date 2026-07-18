package com.video.entitlement.module.admin.repository;

import com.video.entitlement.module.admin.entity.AdminUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRoleRepository extends JpaRepository<AdminUserRole, Long> {

    List<AdminUserRole> findByAdminUserId(Long userId);

    Optional<AdminUserRole> findByAdminUserIdAndRoleId(Long userId, Long roleId);

    @Modifying
    @Transactional
    void deleteByAdminUserId(Long userId);
}
