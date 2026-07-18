package com.video.entitlement.module.entitlement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户权益关联表 —— 记录用户拥有的权益及获取信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_entitlement")
public class UserEntitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entitlement_id", nullable = false)
    private Long entitlementId;

    /** 权益代码（冗余字段，方便直接查询） */
    @Column(name = "entitlement_code", nullable = false, length = 8)
    private String entitlementCode;

    @Column(name = "obtained_at", nullable = false)
    @Builder.Default
    private LocalDateTime obtainedAt = LocalDateTime.now();

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    @Column(nullable = false, length = 16)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
