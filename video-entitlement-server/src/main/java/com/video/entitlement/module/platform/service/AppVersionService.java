package com.video.entitlement.module.platform.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.module.platform.dto.AppVersionRequest;
import com.video.entitlement.module.platform.dto.AppVersionVO;
import com.video.entitlement.module.platform.entity.AppVersion;
import com.video.entitlement.module.platform.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    /**
     * 客户端检查最新版本
     */
    public AppVersionVO getLatestVersion() {
        return appVersionRepository.findTopByStatusOrderByVersionCodeDesc("ACTIVE")
                .map(this::toVO)
                .orElse(null);
    }

    /**
     * 管理端：获取所有版本
     */
    public List<AppVersionVO> listAll() {
        return appVersionRepository.findAll()
                .stream()
                .map(this::toVO)
                .sorted((a, b) -> Integer.compare(b.getVersionCode(), a.getVersionCode()))
                .toList();
    }

    /**
     * 管理端：发布新版本
     */
    @Transactional
    public AppVersionVO create(AppVersionRequest req) {
        if (req.getVersionCode() == null || req.getVersionCode() <= 0) {
            throw new BusinessException(400, "versionCode 必须为正整数");
        }
        if (req.getVersionName() == null || req.getVersionName().isBlank()) {
            throw new BusinessException(400, "versionName 不能为空");
        }

        // 检查 versionCode 唯一
        appVersionRepository.findByVersionCode(req.getVersionCode()).ifPresent(v -> {
            throw new BusinessException(400, "versionCode " + req.getVersionCode() + " 已存在");
        });

        AppVersion entity = AppVersion.builder()
                .versionName(req.getVersionName())
                .versionCode(req.getVersionCode())
                .downloadUrl(req.getDownloadUrl())
                .releaseNotes(req.getReleaseNotes())
                .forceUpdate(req.getForceUpdate() != null && req.getForceUpdate())
                .status("ACTIVE")
                .build();

        return toVO(appVersionRepository.save(entity));
    }

    /**
     * 管理端：更新版本
     */
    @Transactional
    public AppVersionVO update(Long id, AppVersionRequest req) {
        AppVersion entity = appVersionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "版本记录不存在"));

        if (req.getVersionName() != null && !req.getVersionName().isBlank()) {
            entity.setVersionName(req.getVersionName());
        }
        if (req.getVersionCode() != null && req.getVersionCode() > 0
                && !req.getVersionCode().equals(entity.getVersionCode())) {
            appVersionRepository.findByVersionCode(req.getVersionCode()).ifPresent(v -> {
                if (!v.getId().equals(id)) {
                    throw new BusinessException(400, "versionCode " + req.getVersionCode() + " 已存在");
                }
            });
            entity.setVersionCode(req.getVersionCode());
        }
        if (req.getDownloadUrl() != null) {
            entity.setDownloadUrl(req.getDownloadUrl());
        }
        if (req.getReleaseNotes() != null) {
            entity.setReleaseNotes(req.getReleaseNotes());
        }
        if (req.getForceUpdate() != null) {
            entity.setForceUpdate(req.getForceUpdate());
        }

        return toVO(appVersionRepository.save(entity));
    }

    /**
     * 管理端：切换版本状态
     */
    @Transactional
    public AppVersionVO toggleStatus(Long id, String status) {
        AppVersion entity = appVersionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "版本记录不存在"));
        entity.setStatus(status);
        return toVO(appVersionRepository.save(entity));
    }

    private AppVersionVO toVO(AppVersion entity) {
        return AppVersionVO.builder()
                .id(entity.getId())
                .versionName(entity.getVersionName())
                .versionCode(entity.getVersionCode())
                .downloadUrl(entity.getDownloadUrl())
                .releaseNotes(entity.getReleaseNotes())
                .forceUpdate(entity.getForceUpdate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
