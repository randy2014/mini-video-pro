package com.video.entitlement.module.risk.entity;

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
@Table(name = "risk_event")
public class RiskEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "risk_event_type", nullable = false, length = 64)
    private String riskEventType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "device_id", length = 128)
    private String deviceId;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "risk_level", length = 16)
    private String riskLevel;

    @Column(length = 32)
    private String action;

    @Column(columnDefinition = "JSON")
    private String evidence;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
