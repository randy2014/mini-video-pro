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

@Service
@RequiredArgsConstructor
public class EntitlementService {

    private final EntitlementRepository entitlementRepository;

    public Page<EntitlementVO> list(Pageable pageable) {
        return entitlementRepository.findAll(pageable).map(this::toVO);
    }

    public EntitlementVO get(Long id) {
        return toVO(entitlementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "权益不存在")));
    }

    @Transactional
    public EntitlementVO create(EntitlementRequest req) {
        if (entitlementRepository.findByEntitlementCode(req.getEntitlementCode()).isPresent()) {
            throw new BusinessException(ErrorCode.RULE_CONFLICT);
        }
        Entitlement e = Entitlement.builder()
                .entitlementName(req.getEntitlementName())
                .entitlementCode(req.getEntitlementCode())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(req.getStatus() != null ? req.getStatus() : "ENABLED")
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
        e.setEntitlementName(req.getEntitlementName());
        e.setEntitlementCode(req.getEntitlementCode());
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
