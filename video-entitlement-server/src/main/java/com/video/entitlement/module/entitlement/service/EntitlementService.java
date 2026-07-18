package com.video.entitlement.module.entitlement.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.common.util.CodeHashUtil;
import com.video.entitlement.module.entitlement.dto.*;
import com.video.entitlement.module.entitlement.entity.*;
import com.video.entitlement.module.entitlement.entity.enums.*;
import com.video.entitlement.module.entitlement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntitlementService {
    private final EntitlementProductRepository productRepo;
    private final EntitlementProductPlatformRepository productPlatformRepo;
    private final EntitlementCodeBatchRepository batchRepo;
    private final EntitlementCodeRepository codeRepo;
    private final UserEntitlementRepository userEntitlementRepo;
    private final EntitlementUsageRepository usageRepo;

    @Value("${app.entitlement-code.hmac-key}")
    private String hmacKey;

    @Transactional
    public EntitlementProductVO createProduct(EntitlementProductVO vo) {
        EntitlementProduct product = EntitlementProduct.builder()
                .productCode("P" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .productName(vo.getProductName()).description(vo.getDescription())
                .validityType(vo.getValidityType()).validDays(vo.getValidDays())
                .dailyUsageLimit(vo.getDailyUsageLimit()).totalUsageLimit(vo.getTotalUsageLimit())
                .deviceLimit(vo.getDeviceLimit()).status(EntitlementProductStatus.DRAFT.getCode()).build();
        product = productRepo.save(product);
        return toProductVO(product);
    }

    @Transactional
    public EntitlementBatchVO createBatch(EntitlementBatchRequest request) {
        EntitlementProduct product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "产品不存在"));
        String batchNo = "B" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        EntitlementCodeBatch batch = EntitlementCodeBatch.builder()
                .batchNo(batchNo).productId(request.getProductId())
                .channelCode(request.getChannelCode()).quantity(request.getQuantity())
                .status(EntitlementBatchStatus.CREATED.getCode()).build();
        batch = batchRepo.save(batch);
        generateCodes(batch.getId(), batchNo, request.getQuantity());
        batch.setGeneratedCount(request.getQuantity());
        batch.setStatus(EntitlementBatchStatus.ACTIVE.getCode());
        batch = batchRepo.save(batch);
        return toBatchVO(batch);
    }

    private void generateCodes(Long batchId, String batchNo, int quantity) {
        for (int i = 0; i < quantity; i++) {
            String plainCode = batchNo + "-" + CodeHashUtil.generateCode(8);
            String codeHash = CodeHashUtil.hashWithHmac(plainCode, hmacKey);
            EntitlementCode code = EntitlementCode.builder()
                    .batchId(batchId).codeHash(codeHash)
                    .codeMasked(CodeHashUtil.maskCode(plainCode))
                    .status(EntitlementCodeStatus.UNUSED.getCode()).build();
            codeRepo.save(code);
        }
    }

    @Transactional
    public RedeemResponse redeem(Long userId, RedeemRequest request) {
        String codeHash = CodeHashUtil.hashWithHmac(request.getCode(), hmacKey);
        // SELECT FOR UPDATE - concurrent protection
        EntitlementCode code = codeRepo.findByCodeHashForUpdate(codeHash)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITLEMENT_CODE_INVALID));

        if (!EntitlementCodeStatus.UNUSED.getCode().equals(code.getStatus())) {
            throw new BusinessException(ErrorCode.ENTITLEMENT_CODE_USED);
        }

        EntitlementCodeBatch batch = batchRepo.findById(code.getBatchId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITLEMENT_CODE_INVALID));
        EntitlementProduct product = productRepo.findById(batch.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITLEMENT_CODE_INVALID));

        if (!EntitlementProductStatus.ACTIVE.getCode().equals(product.getStatus())) {
            throw new BusinessException(ErrorCode.ENTITLEMENT_CODE_INVALID);
        }

        // Mark code as used
        code.setStatus(EntitlementCodeStatus.ACTIVATED.getCode());
        code.setActivatedUserId(userId);
        code.setActivatedAt(LocalDateTime.now());
        codeRepo.save(code);

        // Update batch count
        batch.setActivatedCount(batch.getActivatedCount() + 1);
        batchRepo.save(batch);

        // Create user entitlement
        LocalDateTime effectiveAt = LocalDateTime.now();
        LocalDateTime expiresAt = null;
        if (EntitlementValidityType.AFTER_ACTIVATION.getCode().equals(product.getValidityType()) && product.getValidDays() != null) {
            expiresAt = effectiveAt.plusDays(product.getValidDays());
        } else if (EntitlementValidityType.FIXED_PERIOD.getCode().equals(product.getValidityType())) {
            effectiveAt = product.getValidFrom();
            expiresAt = product.getValidUntil();
        }

        UserEntitlement ue = UserEntitlement.builder()
                .userId(userId).productId(product.getId())
                .sourceType(EntitlementSourceType.CODE.getCode()).sourceId(code.getId())
                .status(EntitlementCodeStatus.ACTIVATED.getCode())
                .effectiveAt(effectiveAt).expiresAt(expiresAt).usedTotal(0).build();
        ue = userEntitlementRepo.save(ue);

        UserEntitlementVO ueVO = UserEntitlementVO.builder()
                .id(ue.getId()).productCode(product.getProductCode())
                .productName(product.getProductName()).status(ue.getStatus())
                .sourceType(ue.getSourceType()).effectiveAt(ue.getEffectiveAt())
                .expiresAt(ue.getExpiresAt()).usedTotal(ue.getUsedTotal()).build();
        return RedeemResponse.builder().userEntitlement(ueVO).build();
    }

    private EntitlementProductVO toProductVO(EntitlementProduct p) {
        return EntitlementProductVO.builder().id(p.getId()).productCode(p.getProductCode())
                .productName(p.getProductName()).description(p.getDescription())
                .validityType(p.getValidityType()).validDays(p.getValidDays())
                .dailyUsageLimit(p.getDailyUsageLimit()).totalUsageLimit(p.getTotalUsageLimit())
                .deviceLimit(p.getDeviceLimit()).status(p.getStatus()).build();
    }

    private EntitlementBatchVO toBatchVO(EntitlementCodeBatch b) {
        return EntitlementBatchVO.builder().id(b.getId()).batchNo(b.getBatchNo())
                .productId(b.getProductId()).channelCode(b.getChannelCode())
                .quantity(b.getQuantity()).generatedCount(b.getGeneratedCount())
                .activatedCount(b.getActivatedCount()).status(b.getStatus())
                .createdAt(b.getCreatedAt()).build();
    }
}
