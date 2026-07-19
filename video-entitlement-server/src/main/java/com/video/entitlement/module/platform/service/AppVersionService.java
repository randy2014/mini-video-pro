package com.video.entitlement.module.platform.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.module.platform.dto.AppVersionRequest;
import com.video.entitlement.module.platform.dto.AppVersionVO;
import com.video.entitlement.module.platform.entity.AppVersion;
import com.video.entitlement.module.platform.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    @Value("${app.apk-dir:/data/video-apk}")
    private String apkDir;

    @Value("${app.download-base:http://43.161.222.78:8082/downloads}")
    private String downloadBase;

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

    /**
     * 管理端：上传 APK 发布新版本（文件可选，不传文件则使用手动下载地址）
     */
    @Transactional
    public AppVersionVO uploadVersion(MultipartFile file, AppVersionRequest req) {
        if (req.getVersionCode() == null || req.getVersionCode() <= 0) {
            throw new BusinessException(400, "versionCode 必须为正整数");
        }
        if (req.getVersionName() == null || req.getVersionName().isBlank()) {
            throw new BusinessException(400, "versionName 不能为空");
        }
        appVersionRepository.findByVersionCode(req.getVersionCode()).ifPresent(v -> {
            throw new BusinessException(400, "versionCode " + req.getVersionCode() + " 已存在");
        });

        String downloadUrl = resolveDownloadUrl(file, req.getDownloadUrl());

        AppVersion entity = AppVersion.builder()
                .versionName(req.getVersionName())
                .versionCode(req.getVersionCode())
                .downloadUrl(downloadUrl)
                .releaseNotes(req.getReleaseNotes())
                .forceUpdate(req.getForceUpdate() != null && req.getForceUpdate())
                .status("ACTIVE")
                .build();
        return toVO(appVersionRepository.save(entity));
    }

    /**
     * 管理端：替换已有版本的 APK 文件
     */
    @Transactional
    public AppVersionVO replaceApk(Long id, MultipartFile file) {
        AppVersion entity = appVersionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "版本记录不存在"));
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "未提供 APK 文件");
        }
        deleteApkFile(entity.getDownloadUrl());
        entity.setDownloadUrl(storeFile(file));
        return toVO(appVersionRepository.save(entity));
    }

    /**
     * 管理端：删除版本（同时删除磁盘上的 APK 文件，仅限下载目录内）
     */
    @Transactional
    public void delete(Long id) {
        AppVersion entity = appVersionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "版本记录不存在"));
        deleteApkFile(entity.getDownloadUrl());
        appVersionRepository.delete(entity);
    }

    private String resolveDownloadUrl(MultipartFile file, String manualUrl) {
        if (file != null && !file.isEmpty()) {
            return storeFile(file);
        }
        if (manualUrl != null && !manualUrl.isBlank()) {
            return manualUrl.trim();
        }
        throw new BusinessException(400, "请上传 APK 文件或填写下载地址");
    }

    private String storeFile(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = ".apk";
        if (original != null && original.contains(".")) {
            String raw = original.substring(original.lastIndexOf(".")).toLowerCase();
            if (raw.equals(".apk")) {
                ext = ".apk";
            }
        }
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String base = "video-entitlement-v" + stamp;
        String name = base + ext;
        try {
            Path dir = Paths.get(apkDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path target = dir.resolve(name).normalize();
            int i = 1;
            while (Files.exists(target)) {
                target = dir.resolve(base + "_" + i + ext).normalize();
                i++;
            }
            file.transferTo(target.toFile());
            String baseUrl = downloadBase.endsWith("/") ? downloadBase.substring(0, downloadBase.length() - 1) : downloadBase;
            return baseUrl + "/" + target.getFileName().toString();
        } catch (Exception e) {
            throw new BusinessException(500, "APK 文件保存失败：" + e.getMessage());
        }
    }

    private void deleteApkFile(String downloadUrl) {
        if (downloadUrl == null || downloadUrl.isBlank()) return;
        String fileName;
        if (downloadUrl.startsWith(downloadBase)) {
            fileName = downloadUrl.substring(downloadBase.length()).replaceAll("^/", "");
        } else if (downloadUrl.contains("/downloads/")) {
            fileName = downloadUrl.substring(downloadUrl.indexOf("/downloads/") + "/downloads/".length());
        } else {
            return;
        }
        if (fileName.isBlank()) return;
        try {
            Path dir = Paths.get(apkDir).normalize();
            Path target = dir.resolve(fileName).normalize();
            if (!target.startsWith(dir)) return; // 路径穿越防护
            Files.deleteIfExists(target);
        } catch (Exception ignored) {
        }
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
