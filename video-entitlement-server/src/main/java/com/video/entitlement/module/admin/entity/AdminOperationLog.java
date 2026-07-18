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
@Table(name = "admin_operation_log")
public class AdminOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_user_id", nullable = false)
    private Long adminUserId;

    @Column(nullable = false, length = 64)
    private String module;

    @Column(nullable = false, length = 64)
    private String operation;

    @Column(name = "business_id", length = 64)
    private String businessId;

    @Column(name = "before_json", columnDefinition = "JSON")
    private String beforeJson;

    @Column(name = "after_json", columnDefinition = "JSON")
    private String afterJson;

    @Column(length = 32)
    private String result;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
