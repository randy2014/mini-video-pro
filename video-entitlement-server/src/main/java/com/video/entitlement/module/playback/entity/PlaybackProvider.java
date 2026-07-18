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
@Table(name = "playback_provider")
public class PlaybackProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_code", nullable = false, unique = true, length = 64)
    private String providerCode;

    @Column(name = "provider_name", nullable = false, length = 128)
    private String providerName;

    @Column(name = "provider_type", nullable = false, length = 32)
    private String providerType;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "authorization_status", nullable = false, length = 32)
    @Builder.Default
    private String authorizationStatus = "PENDING";

    @Column(name = "authorization_expiry")
    private LocalDateTime authorizationExpiry;

    @Column(name = "config_json", columnDefinition = "JSON")
    private String configJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
