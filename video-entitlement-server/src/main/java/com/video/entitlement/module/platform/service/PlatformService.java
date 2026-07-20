package com.video.entitlement.module.platform.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.module.platform.dto.*;
import com.video.entitlement.module.platform.entity.*;
import com.video.entitlement.module.platform.entity.enums.VideoPlatformStatus;
import com.video.entitlement.module.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatformService {
    private final VideoPlatformRepository platformRepo;
    private final VideoPlatformDomainRepository domainRepo;
    private final VideoUrlRuleRepository ruleRepo;

    // ====== Client API ======
    public List<VideoPlatformVO> getActivePlatforms() {
        return platformRepo.findAllByStatus(VideoPlatformStatus.ACTIVE.getCode()).stream()
                .map(this::toVO).collect(Collectors.toList());
    }

    // ====== Admin API ======
    public List<VideoPlatformVO> getAllPlatforms() {
        return platformRepo.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Transactional
    public VideoPlatformVO createPlatform(PlatformConfigRequest request) {
        if (platformRepo.findByPlatformCode(request.getPlatformCode()).isPresent()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "平台代码已存在");
        }
        VideoPlatform platform = VideoPlatform.builder()
                .platformCode(request.getPlatformCode())
                .platformName(request.getPlatformName())
                .platformType(request.getPlatformType() != null ? request.getPlatformType() : "video")
                .homeUrl(request.getHomeUrl())
                .logo(request.getLogo())
                .status(VideoPlatformStatus.ACTIVE.getCode())
                .enabled(true)
                .build();
        platform = platformRepo.save(platform);
        updateDomains(platform.getId(), request.getDomains());
        return toVO(platform);
    }

    @Transactional
    public VideoPlatformVO updatePlatform(Long id, PlatformConfigRequest request) {
        VideoPlatform platform = platformRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "平台不存在"));
        if (request.getPlatformName() != null) platform.setPlatformName(request.getPlatformName());
        if (request.getPlatformType() != null) platform.setPlatformType(request.getPlatformType());
        if (request.getHomeUrl() != null) platform.setHomeUrl(request.getHomeUrl());
        if (request.getLogo() != null) platform.setLogo(request.getLogo());
        platform = platformRepo.save(platform);
        if (request.getDomains() != null) {
            updateDomains(platform.getId(), request.getDomains());
        }
        return toVO(platform);
    }

    @Transactional
    public void deletePlatform(Long id) {
        VideoPlatform platform = platformRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "平台不存在"));
        domainRepo.deleteByPlatformId(platform.getId());
        ruleRepo.deleteByPlatformId(platform.getId());
        platformRepo.delete(platform);
    }

    private void updateDomains(Long platformId, List<String> domains) {
        domainRepo.deleteByPlatformId(platformId);
        if (domains != null) {
            for (String domain : domains) {
                domainRepo.save(VideoPlatformDomain.builder()
                        .platformId(platformId).host(domain).scheme("https").enabled(true).build());
            }
        }
    }

    public VideoPlatform getById(Long id) {
        return platformRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "平台不存在"));
    }

    private VideoPlatformVO toVO(VideoPlatform p) {
        List<String> domainList = domainRepo.findByPlatformId(p.getId()).stream()
                .map(VideoPlatformDomain::getHost).collect(Collectors.toList());
        return VideoPlatformVO.builder()
                .id(p.getId())
                .platformCode(p.getPlatformCode())
                .platformName(p.getPlatformName())
                .platformType(p.getPlatformType())
                .homeUrl(p.getHomeUrl())
                .logo(p.getLogo())
                .status(p.getStatus())
                .domains(domainList)
                .build();
    }
}
