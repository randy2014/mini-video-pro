package com.video.entitlement.module.user.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.module.user.entity.InviteCode;
import com.video.entitlement.module.user.entity.InviteRecord;
import com.video.entitlement.module.user.repository.InviteCodeRepository;
import com.video.entitlement.module.user.repository.InviteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteCodeRepository codeRepo;
    private final InviteRecordRepository recordRepo;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RNG = new SecureRandom();

    public record InviteStats(String code, int totalUsed, int rewardDays) {}

    /** 获取或生成 + 统计信息 */
    public InviteStats getOrCreateCodeAndStats(Long userId) {
        InviteCode ic = getOrCreateCode(userId);
        return new InviteStats(ic.getCode(), ic.getTotalUsed(), ic.getRewardDays());
    }

    /** 获取或生成用户邀请码 */
    @Transactional
    public InviteCode getOrCreateCode(Long userId) {
        return codeRepo.findByUserId(userId)
                .orElseGet(() -> {
                    String code = generateUniqueCode();
                    return codeRepo.save(InviteCode.builder()
                            .userId(userId).code(code).build());
                });
    }

    /** 被邀请人使用邀请码激活 */
    @Transactional
    public InviteRecord activateInvite(String code, Long inviteeUserId) {
        InviteCode invite = codeRepo.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "邀请码无效"));

        if (invite.getUserId().equals(inviteeUserId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "不能使用自己的邀请码");
        }

        InviteRecord record = InviteRecord.builder()
                .code(invite.getCode())
                .inviterUserId(invite.getUserId())
                .inviteeUserId(inviteeUserId)
                .rewardDays(invite.getRewardDays())
                .build();
        recordRepo.save(record);

        invite.setTotalUsed(invite.getTotalUsed() + 1);
        codeRepo.save(invite);

        return record;
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) sb.append(CHARS.charAt(RNG.nextInt(CHARS.length())));
            String code = sb.toString();
            if (codeRepo.findByCode(code).isEmpty()) return code;
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成邀请码失败，请重试");
    }
}
