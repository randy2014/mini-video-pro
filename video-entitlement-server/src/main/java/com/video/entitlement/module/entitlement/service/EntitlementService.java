package com.video.entitlement.module.entitlement.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.module.entitlement.dto.EntitlementRequest;
import com.video.entitlement.module.entitlement.dto.EntitlementVO;
import com.video.entitlement.module.entitlement.entity.Entitlement;
import com.video.entitlement.module.entitlement.repository.EntitlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntitlementService {

    private final EntitlementRepository entitlementRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    public Page<EntitlementVO> list(Pageable pageable) {
        return entitlementRepository.findAll(pageable).map(this::toVO);
    }

    public EntitlementVO get(Long id) {
        return toVO(entitlementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "权益不存在")));
    }

    @Transactional
    public EntitlementVO create(EntitlementRequest req) {
        // 自动生成8位不重复权益编码
        String code = generateUniqueCode();

        // 校验：启用状态必须有开始和结束时间
        validateStatus(req.getStatus(), req.getStartTime(), req.getEndTime());

        Entitlement e = Entitlement.builder()
                .entitlementName(req.getEntitlementName())
                .entitlementCode(code)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(req.getStatus() != null ? req.getStatus() : "DISABLED")
                .ownerName(req.getOwnerName())
                .ownerPhone(req.getOwnerPhone())
                .ownerProfession(req.getOwnerProfession())
                .build();
        return toVO(entitlementRepository.save(e));
    }

    @Transactional
    public EntitlementVO update(Long id, EntitlementRequest req) {
        Entitlement e = entitlementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "权益不存在"));

        // 校验：启用状态必须有开始和结束时间
        validateStatus(req.getStatus(), req.getStartTime(), req.getEndTime());

        e.setEntitlementName(req.getEntitlementName());
        e.setStartTime(req.getStartTime());
        e.setEndTime(req.getEndTime());
        e.setOwnerName(req.getOwnerName());
        e.setOwnerPhone(req.getOwnerPhone());
        e.setOwnerProfession(req.getOwnerProfession());
        if (req.getStatus() != null) e.setStatus(req.getStatus());
        return toVO(entitlementRepository.save(e));
    }

    @Transactional
    public void delete(Long id) {
        if (!entitlementRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "权益不存在");
        }
        entitlementRepository.deleteById(id);
    }

    /** 自动生成8位不重复数字编码 */
    private String generateUniqueCode() {
        Set<String> existing = entitlementRepository.findAll()
                .stream().map(Entitlement::getEntitlementCode)
                .collect(Collectors.toSet());
        String code;
        int maxAttempts = 100;
        do {
            code = String.format("%08d", RANDOM.nextInt(100000000));
            maxAttempts--;
        } while (existing.contains(code) && maxAttempts > 0);
        if (maxAttempts == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "权益码生成失败，请重试");
        }
        return code;
    }

    /** 启用状态必须设置开始和结束时间 */
    private void validateStatus(String status, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        if ("ENABLED".equals(status) && (startTime == null || endTime == null)) {
            throw new BusinessException(400, "启用状态必须设置开始时间和结束时间");
        }
    }

    private EntitlementVO toVO(Entitlement e) {
        return EntitlementVO.builder()
                .id(e.getId()).entitlementName(e.getEntitlementName())
                .entitlementCode(e.getEntitlementCode()).startTime(e.getStartTime())
                .endTime(e.getEndTime()).status(e.getStatus())
                .ownerName(e.getOwnerName()).ownerPhone(e.getOwnerPhone())
                .ownerProfession(e.getOwnerProfession()).createdAt(e.getCreatedAt())
                .build();
    }
}
