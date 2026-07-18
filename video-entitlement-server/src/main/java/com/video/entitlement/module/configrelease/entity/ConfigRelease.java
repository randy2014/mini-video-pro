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
@Table(name = "config_release")
public class ConfigRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "release_no", nullable = false, unique = true, length = 64)
    private String releaseNo;

    @Column(name = "release_type", nullable = false, length = 32)
    private String releaseType;

    @Column(name = "config_version", nullable = false, length = 64)
    private String configVersion;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "DRAFT";

    @Column(name = "gray_percentage", nullable = false)
    @Builder.Default
    private Integer grayPercentage = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
