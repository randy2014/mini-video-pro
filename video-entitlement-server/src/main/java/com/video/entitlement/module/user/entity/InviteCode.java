package com.video.entitlement.module.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "invite_code")
public class InviteCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @Column(name = "total_used", nullable = false)
    @Builder.Default
    private Integer totalUsed = 0;

    @Column(name = "reward_days", nullable = false)
    @Builder.Default
    private Integer rewardDays = 3;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
