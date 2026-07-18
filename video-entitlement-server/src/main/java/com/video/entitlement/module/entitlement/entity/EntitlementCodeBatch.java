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
@Table(name = "entitlement_code_batch")
public class EntitlementCodeBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_no", nullable = false, unique = true, length = 64)
    private String batchNo;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "channel_code", length = 64)
    private String channelCode;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "generated_count", nullable = false)
    @Builder.Default
    private Integer generatedCount = 0;

    @Column(name = "activated_count", nullable = false)
    @Builder.Default
    private Integer activatedCount = 0;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "CREATED";

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
