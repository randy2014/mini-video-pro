package com.video.entitlement.module.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_role_permission")
@IdClass(AdminRolePermission.AdminRolePermissionId.class)
public class AdminRolePermission {

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Id
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminRolePermissionId implements Serializable {
        private Long roleId;
        private Long permissionId;
    }
}
