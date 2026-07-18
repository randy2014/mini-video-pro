package com.video.entitlement.module.platform.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.common.util.UrlValidator;
import com.video.entitlement.module.platform.dto.*;
import com.video.entitlement.module.platform.entity.*;
import com.video.entitlement.module.platform.entity.enums.UrlRuleType;
import com.video.entitlement.module.platform.entity.enums.VideoPlatformStatus;
import com.video.entitlement.module.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatformService {
    private final VideoPlatformRepository platformRepo;
    private final VideoPlatformDomainRepository domainRepo;
    private final VideoUrlRuleRepository ruleRepo;

    public List<VideoPlatformVO> getActivePlatforms() {
        return platformRepo.findAllByStatus(VideoPlatformStatus.ACTIVE.getCode()).stream()
                .map(this::toVO).collect(Collectors.toList());
    }

    @Transactional
    public VideoPlatformVO createPlatform(PlatformConfigRequest request) {
        if (platformRepo.findByPlatformCode(request.getPlatformCode()).isPresent()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "平台已存在");
        }
        VideoPlatform platform = VideoPlatform.builder()
                .platformCode(request.getPlatformCode()).platformName(request.getPlatformName())
                .homeUrl(request.getHomeUrl()).status(VideoPlatformStatus.ACTIVE.getCode())
                .enabled(true).build();
        platform = platformRepo.save(platform);
        if (request.getDomains() != null) {
            for (String domain : request.getDomains()) {
                domainRepo.save(VideoPlatformDomain.builder()
                        .platformId(platform.getId()).host(domain).scheme("https").enabled(true).build());
            }
        }
        return toVO(platform);
    }

    public UrlStandardizeResponse standardizeUrl(UrlStandardizeRequest request) {
        if (!UrlValidator.isValidHttpsUrl(request.getUrl())) {
            throw new BusinessException(ErrorCode.URL_INVALID);
        }
        String host = UrlValidator.extractHost(request.getUrl());
        if (host == null) throw new BusinessException(ErrorCode.URL_INVALID);

        // Check domains
        List<VideoPlatformDomain> domains = domainRepo.findAll();
        boolean found = domains.stream().anyMatch(d -> d.getEnabled() &&
                (d.getHost().equals(host) || (d.getIncludeSubdomains() && host.endsWith("." + d.getHost()))));
        if (!found) {
            throw new BusinessException(ErrorCode.URL_HOST_NOT_ALLOWED);
        }

        String canonicalUrl = UrlValidator.normalizeUrl(request.getUrl());
        String contentKey = extractContentKey(canonicalUrl);

        // Match rules
        boolean matched = false;
        String platformCode = null;
        for (VideoPlatformDomain d : domains) {
            if (d.getHost().equals(host) || host.endsWith("." + d.getHost())) {
                VideoPlatform platform = platformRepo.findById(d.getPlatformId()).orElse(null);
                if (platform != null) {
                    platformCode = platform.getPlatformCode();
                    List<VideoUrlRule> rules = ruleRepo.findByPlatformIdAndEnabledOrderByPriorityAsc(platform.getId(), true);
                    for (VideoUrlRule rule : rules) {
                        try {
                            if (rule.getPattern() != null && Pattern.compile(rule.getPattern()).matcher(canonicalUrl).find()) {
                                matched = true;
                                break;
                            }
                            if (UrlRuleType.PREFIX.getCode().equals(rule.getRuleType()) && canonicalUrl.startsWith(rule.getPattern())) {
                                matched = true;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                }
                break;
            }
        }

        return UrlStandardizeResponse.builder()
                .canonicalUrl(canonicalUrl).contentKey(contentKey)
                .platformCode(platformCode).matched(matched).build();
    }

    private String extractContentKey(String url) {
        try {
            String path = new URI(url).getPath();
            return path != null ? path : "/";
        } catch (Exception e) {
            return url;
        }
    }

    private VideoPlatformVO toVO(VideoPlatform p) {
        List<String> domainList = domainRepo.findByPlatformId(p.getId()).stream()
                .map(VideoPlatformDomain::getHost).collect(Collectors.toList());
        return VideoPlatformVO.builder().platformCode(p.getPlatformCode())
                .platformName(p.getPlatformName()).homeUrl(p.getHomeUrl())
                .status(p.getStatus()).domains(domainList).build();
    }
}
