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
@Table(name = "playback_result")
public class PlaybackResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "playback_request_id", nullable = false, unique = true)
    private Long playbackRequestId;

    @Column(name = "final_status", nullable = false, length = 32)
    private String finalStatus;

    @Column(name = "successful_route_id")
    private Long successfulRouteId;

    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(name = "total_duration_ms")
    private Long totalDurationMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
