package com.video.entitlement.module.playback.entity;

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
@Table(name = "playback_rule")
public class PlaybackRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_code", nullable = false, length = 64)
    private String platformCode;

    @Column(name = "client_type", length = 32)
    private String clientType;

    @Column(name = "version_range", length = 64)
    private String versionRange;

    @Column(name = "entitlement_product_id")
    private Long entitlementProductId;

    @Column(name = "route_group_id")
    private Long routeGroupId;

    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
