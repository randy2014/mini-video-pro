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
@Table(name = "video_platform_domain")
public class VideoPlatformDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_id", nullable = false)
    private Long platformId;

    @Column(nullable = false, length = 16)
    @Builder.Default
    private String scheme = "https";

    @Column(nullable = false, length = 255)
    private String host;

    @Column(name = "include_subdomains", nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean includeSubdomains = false;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
