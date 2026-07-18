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
@Table(name = "admin_user_role")
@IdClass(AdminUserRole.AdminUserRoleId.class)
public class AdminUserRole {

    @Id
    @Column(name = "admin_user_id", nullable = false)
    private Long adminUserId;

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUserRoleId implements Serializable {
        private Long adminUserId;
        private Long roleId;
    }
}
