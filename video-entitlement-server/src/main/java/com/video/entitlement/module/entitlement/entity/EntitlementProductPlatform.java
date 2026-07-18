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
@Table(name = "entitlement_product_platform")
public class EntitlementProductPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "platform_code", nullable = false, length = 64)
    private String platformCode;

    @Column(name = "scope_type", length = 32)
    private String scopeType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
