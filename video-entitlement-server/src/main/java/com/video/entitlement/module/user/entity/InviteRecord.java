package com.video.entitlement.module.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "invite_record")
public class InviteRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String code;

    @Column(name = "inviter_user_id", nullable = false)
    private Long inviterUserId;

    @Column(name = "invitee_user_id", nullable = false)
    private Long inviteeUserId;

    @Column(name = "reward_days", nullable = false)
    @Builder.Default
    private Integer rewardDays = 3;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
