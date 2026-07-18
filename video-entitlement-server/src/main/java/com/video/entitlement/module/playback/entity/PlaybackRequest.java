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
@Table(name = "playback_request")
public class PlaybackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_no", nullable = false, unique = true, length = 64)
    private String requestNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "platform_code", nullable = false, length = 64)
    private String platformCode;

    @Column(name = "content_key", length = 255)
    private String contentKey;

    @Column(name = "canonical_url", columnDefinition = "TEXT")
    private String canonicalUrl;

    @Column(name = "original_url", columnDefinition = "TEXT")
    private String originalUrl;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "CREATED";

    @Column(name = "maximum_attempts", nullable = false)
    @Builder.Default
    private Integer maximumAttempts = 3;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
