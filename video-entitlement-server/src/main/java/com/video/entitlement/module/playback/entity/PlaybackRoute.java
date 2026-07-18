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
@Table(name = "playback_route")
public class PlaybackRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_code", nullable = false, unique = true, length = 64)
    private String routeCode;

    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "route_type", nullable = false, length = 32)
    private String routeType;

    @Column(name = "target_template", columnDefinition = "TEXT")
    private String targetTemplate;

    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "allowed_hosts", columnDefinition = "JSON")
    private String allowedHosts;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "authorization_status", nullable = false, length = 32)
    @Builder.Default
    private String authorizationStatus = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
