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
@Table(name = "playback_route_group")
public class PlaybackRouteGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_code", nullable = false, unique = true, length = 64)
    private String groupCode;

    @Column(name = "platform_code", nullable = false, length = 64)
    private String platformCode;

    @Column(name = "selection_strategy", nullable = false, length = 32)
    @Builder.Default
    private String selectionStrategy = "PRIORITY";

    @Column(name = "maximum_attempts", nullable = false)
    @Builder.Default
    private Integer maximumAttempts = 3;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
