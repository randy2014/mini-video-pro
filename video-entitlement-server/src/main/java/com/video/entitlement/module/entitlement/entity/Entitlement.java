package com.video.entitlement.module.entitlement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 权益表 —— 定义可被用户持有的权益（产品/会员权益）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "entitlement")
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 权益名称 */
    @Column(name = "entitlement_name", nullable = false, length = 128)
    private String entitlementName;

    /** 权益代码（8位数字） */
    @Column(name = "entitlement_code", nullable = false, length = 8)
    private String entitlementCode;

    /** 权益开始时间 */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /** 权益结束时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** 权益状态：ENABLED 启用 / DISABLED 禁用 */
    @Column(nullable = false, length = 16)
    @Builder.Default
    private String status = "ENABLED";

    /** 权益拥有人 */
    @Column(name = "owner_name", length = 64)
    private String ownerName;

    /** 权益拥有人电话 */
    @Column(name = "owner_phone", length = 32)
    private String ownerPhone;

    /** 权益拥有人职业 */
    @Column(name = "owner_profession", length = 64)
    private String ownerProfession;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
