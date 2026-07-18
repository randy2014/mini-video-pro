package com.video.entitlement.module.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_permission")
public class AdminPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_code", nullable = false, unique = true, length = 128)
    private String permissionCode;

    @Column(name = "permission_name", nullable = false, length = 128)
    private String permissionName;

    @Column(name = "resource_type", length = 64)
    private String resourceType;

    @Column(length = 255)
    private String path;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
