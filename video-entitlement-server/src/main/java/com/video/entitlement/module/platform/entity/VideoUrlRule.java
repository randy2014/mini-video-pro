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
@Table(name = "video_url_rule")
public class VideoUrlRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_id", nullable = false)
    private Long platformId;

    @Column(name = "rule_type", nullable = false, length = 32)
    private String ruleType;

    @Column(nullable = false, length = 512)
    private String pattern;

    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "extract_config", columnDefinition = "JSON")
    private String extractConfig;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
