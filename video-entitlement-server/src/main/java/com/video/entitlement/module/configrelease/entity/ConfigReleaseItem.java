package com.video.entitlement.module.configrelease.entity;

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
@Table(name = "config_release_item")
public class ConfigReleaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "release_id", nullable = false)
    private Long releaseId;

    @Column(name = "config_type", nullable = false, length = 32)
    private String configType;

    @Column(name = "business_id", nullable = false, length = 128)
    private String businessId;

    @Column(name = "content_json", columnDefinition = "JSON")
    private String contentJson;

    @Column(name = "content_hash", length = 128)
    private String contentHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
