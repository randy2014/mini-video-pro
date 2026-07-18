package com.video.entitlement.module.entitlement.entity;

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
@Table(name = "entitlement_code")
public class EntitlementCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "code_hash", nullable = false, unique = true, length = 128)
    private String codeHash;

    @Column(name = "code_masked", nullable = false, length = 64)
    private String codeMasked;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "UNUSED";

    @Column(name = "activated_user_id")
    private Long activatedUserId;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
