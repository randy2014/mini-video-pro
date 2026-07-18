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
@Table(name = "playback_attempt")
public class PlaybackAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "playback_request_id", nullable = false)
    private Long playbackRequestId;

    @Column(name = "attempt_no", nullable = false)
    @Builder.Default
    private Integer attemptNo = 1;

    @Column(name = "route_id")
    private Long routeId;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String result = "PENDING";

    @Column(name = "error_type", length = 64)
    private String errorType;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
