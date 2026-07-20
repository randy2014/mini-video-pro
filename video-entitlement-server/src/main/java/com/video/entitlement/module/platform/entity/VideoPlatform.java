package com.video.entitlement.module.platform.entity;

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
@Table(name = "video_platform")
public class VideoPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_code", nullable = false, unique = true, length = 64)
    private String platformCode;

    @Column(name = "platform_name", nullable = false, length = 128)
    private String platformName;

    @Column(name = "platform_type", length = 32)
    @Builder.Default
    private String platformType = "video";

    @Column(name = "home_url", length = 512)
    private String homeUrl;

    @Column(name = "logo_url", length = 512)
    private String logo;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "minimum_android_version", length = 32)
    private String minimumAndroidVersion;

    @Column(name = "minimum_ios_version", length = 32)
    private String minimumIosVersion;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
