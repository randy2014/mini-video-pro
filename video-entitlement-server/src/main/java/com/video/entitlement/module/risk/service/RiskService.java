package com.video.entitlement.module.risk.service;

import com.video.entitlement.module.risk.entity.RiskBlacklist;
import com.video.entitlement.module.risk.entity.RiskEvent;
import com.video.entitlement.module.risk.entity.enums.RiskAction;
import com.video.entitlement.module.risk.entity.enums.RiskBlacklistType;
import com.video.entitlement.module.risk.entity.enums.RiskEventType;
import com.video.entitlement.module.risk.repository.RiskBlacklistRepository;
import com.video.entitlement.module.risk.repository.RiskEventRepository;
import com.video.entitlement.module.risk.repository.RiskRuleRepository;
import com.video.entitlement.module.risk.dto.RiskEventVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskBlacklistRepository blacklistRepo;
    private final RiskEventRepository eventRepo;
    private final RiskRuleRepository ruleRepo;
    private final StringRedisTemplate redisTemplate;

    public boolean isBlocked(String type, String value) {
        return blacklistRepo.findByBlacklistTypeAndTargetValue(type, value)
                .map(b -> "ACTIVE".equals(b.getStatus())).orElse(false);
    }

    public boolean checkRateLimit(String key, int replenishRate, int burstCapacity) {
        String redisKey = "rate:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, 1, TimeUnit.SECONDS);
        }
        return count != null && count <= burstCapacity;
    }

    public void logEvent(RiskEventType eventType, Long userId, String deviceId, String ip, RiskAction action) {
        eventRepo.save(RiskEvent.builder()
                .riskEventType(eventType.getCode()).userId(userId)
                .deviceId(deviceId).ipAddress(ip)
                .riskLevel("MEDIUM").action(action.getCode()).build());
    }

    public Page<RiskEventVO> getEvents(Long userId, Pageable pageable) {
        return eventRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(e -> RiskEventVO.builder().id(e.getId())
                        .riskEventType(e.getRiskEventType()).userId(e.getUserId())
                        .riskLevel(e.getRiskLevel()).action(e.getAction())
                        .createdAt(e.getCreatedAt()).build());
    }
}
