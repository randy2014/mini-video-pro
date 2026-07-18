package com.video.entitlement.module.playback.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.module.entitlement.entity.UserEntitlement;
import com.video.entitlement.module.entitlement.entity.enums.UserEntitlementStatus;
import com.video.entitlement.module.entitlement.repository.UserEntitlementRepository;
import com.video.entitlement.module.playback.dto.*;
import com.video.entitlement.module.playback.entity.*;
import com.video.entitlement.module.playback.entity.enums.*;
import com.video.entitlement.module.playback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaybackService {
    private final PlaybackRuleRepository ruleRepo;
    private final PlaybackRouteGroupRepository groupRepo;
    private final PlaybackRouteRepository routeRepo;
    private final PlaybackRequestRepository requestRepo;
    private final PlaybackAttemptRepository attemptRepo;
    private final PlaybackResultRepository resultRepo;
    private final UserEntitlementRepository userEntitlementRepo;

    @Transactional
    public PlaybackDecisionVO resolve(Long userId, PlaybackResolveRequest req) {
        // 1. Check entitlement
        List<UserEntitlement> entitlements = userEntitlementRepo.findByUserIdAndStatus(userId, UserEntitlementStatus.ACTIVE.getCode());
        if (entitlements.isEmpty()) {
            throw new BusinessException(ErrorCode.ENTITLEMENT_NOT_FOUND);
        }
        // Check expiration and effectivity
        LocalDateTime now = LocalDateTime.now();
        UserEntitlement validEnt = entitlements.stream()
                .filter(e -> (e.getEffectiveAt() == null || !now.isBefore(e.getEffectiveAt())))
                .filter(e -> (e.getExpiresAt() == null || !now.isAfter(e.getExpiresAt())))
                .findFirst().orElse(null);
        if (validEnt == null) {
            // Check if any expired
            boolean hasExpired = entitlements.stream().anyMatch(e -> e.getExpiresAt() != null && now.isAfter(e.getExpiresAt()));
            throw new BusinessException(hasExpired ? ErrorCode.ENTITLEMENT_EXPIRED : ErrorCode.ENTITLEMENT_NOT_EFFECTIVE);
        }

        // 2. Match rules
        List<PlaybackRule> rules = ruleRepo.findByPlatformCodeAndEnabledOrderByPriorityAsc(req.getPlatformCode(), true);
        if (rules.isEmpty()) {
            throw new BusinessException(ErrorCode.PLAYBACK_RULE_NOT_FOUND);
        }
        PlaybackRule matchedRule = rules.get(0);

        // 3. Get route group
        PlaybackRouteGroup group = groupRepo.findById(matchedRule.getRouteGroupId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYBACK_ROUTE_NOT_FOUND));

        // 4. Get available routes
        List<PlaybackRoute> routes = routeRepo.findByGroupIdAndEnabledOrderByPriorityAsc(group.getId(), true)
                .stream().filter(r -> AuthorizationStatus.VERIFIED.getCode().equals(r.getAuthorizationStatus()))
                .collect(Collectors.toList());
        if (routes.isEmpty()) {
            throw new BusinessException(ErrorCode.PLAYBACK_ROUTE_UNAVAILABLE);
        }

        // 5. Create request
        String requestNo = "PR" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        PlaybackRequest request = PlaybackRequest.builder()
                .requestNo(requestNo).userId(userId)
                .platformCode(req.getPlatformCode()).contentKey(req.getContentKey())
                .canonicalUrl(req.getCanonicalUrl()).originalUrl(req.getOriginalUrl())
                .status(PlaybackRequestStatus.CREATED.getCode())
                .maximumAttempts(group.getMaximumAttempts()).build();
        request = requestRepo.save(request);

        // 6. Select route & create attempt
        PlaybackRoute selectedRoute = selectRoute(routes, group.getSelectionStrategy());
        PlaybackAttempt attempt = PlaybackAttempt.builder()
                .playbackRequestId(request.getId()).attemptNo(1)
                .routeId(selectedRoute.getId())
                .result(PlaybackAttemptResult.PENDING.getCode()).build();
        attemptRepo.save(attempt);

        // 7. Update request status
        request.setStatus(PlaybackRequestStatus.RESOLVED.getCode());
        requestRepo.save(request);

        // 8. Build decision
        String decisionType = selectedRoute.getRouteType();
        String targetUrl = selectedRoute.getTargetTemplate() != null
                ? selectedRoute.getTargetTemplate().replace("{contentKey}", req.getContentKey())
                : null;
        boolean hasNext = routes.size() > 1 && attempt.getAttemptNo() < group.getMaximumAttempts();

        return PlaybackDecisionVO.builder()
                .requestId(requestNo).decisionType(decisionType)
                .targetUrl(targetUrl).attemptNo(1).hasNext(hasNext).build();
    }

    @Transactional
    public PlaybackDecisionVO reportResult(PlaybackReportRequest req) {
        PlaybackRequest request = requestRepo.findByRequestNo(req.getRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYBACK_REQUEST_NOT_FOUND));

        PlaybackAttempt attempt = attemptRepo.findByPlaybackRequestIdAndAttemptNo(request.getId(), req.getAttemptNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.PLAYBACK_REQUEST_NOT_FOUND));

        // Idempotency check
        if (!PlaybackAttemptResult.PENDING.getCode().equals(attempt.getResult())) {
            return buildExistingDecision(request, attempt);
        }

        // Update attempt
        attempt.setResult(req.getResult());
        attempt.setErrorType(req.getErrorType());
        attempt.setDurationMs(req.getDurationMs());
        attemptRepo.save(attempt);

        if (PlaybackReportResult.SUCCESS.getCode().equals(req.getResult())) {
            request.setStatus(PlaybackRequestStatus.SUCCESS.getCode());
            requestRepo.save(request);
            resultRepo.save(PlaybackResult.builder()
                    .playbackRequestId(request.getId()).finalStatus("SUCCESS")
                    .successfulRouteId(attempt.getRouteId()).attemptCount(req.getAttemptNo())
                    .totalDurationMs(attempt.getDurationMs()).build());
            return PlaybackDecisionVO.builder().requestId(req.getRequestId())
                    .decisionType("SUCCESS").hasNext(false).build();
        }

        if (PlaybackReportResult.CANCELLED.getCode().equals(req.getResult())) {
            request.setStatus(PlaybackRequestStatus.CANCELLED.getCode());
            requestRepo.save(request);
            return PlaybackDecisionVO.builder().requestId(req.getRequestId())
                    .decisionType("CANCELLED").hasNext(false).build();
        }

        // FAILED - try next
        if (req.getAttemptNo() >= request.getMaximumAttempts()) {
            request.setStatus(PlaybackRequestStatus.FAILED.getCode());
            requestRepo.save(request);
            resultRepo.save(PlaybackResult.builder()
                    .playbackRequestId(request.getId()).finalStatus("FAILED")
                    .attemptCount(req.getAttemptNo()).build());
            return PlaybackDecisionVO.builder().requestId(req.getRequestId())
                    .hasNext(false).message("Maximum attempts reached").build();
        }

        // Get next route
        PlaybackRouteGroup group = routeRepo.findById(attempt.getRouteId())
                .map(r -> groupRepo.findById(r.getGroupId()).orElse(null)).orElse(null);
        if (group == null) {
            throw new BusinessException(ErrorCode.PLAYBACK_ROUTE_NOT_FOUND);
        }

        List<PlaybackRoute> routes = routeRepo.findByGroupIdAndEnabledOrderByPriorityAsc(group.getId(), true);
        int nextAttemptNo = req.getAttemptNo() + 1;
        PlaybackRoute nextRoute = selectRoute(routes, group.getSelectionStrategy());

        PlaybackAttempt nextAttempt = PlaybackAttempt.builder()
                .playbackRequestId(request.getId()).attemptNo(nextAttemptNo)
                .routeId(nextRoute.getId()).result(PlaybackAttemptResult.PENDING.getCode()).build();
        attemptRepo.save(nextAttempt);

        request.setStatus(PlaybackRequestStatus.RESOLVED.getCode());
        requestRepo.save(request);

        String targetUrl = nextRoute.getTargetTemplate() != null
                ? nextRoute.getTargetTemplate().replace("{contentKey}", request.getContentKey())
                : null;
        boolean hasNext = nextAttemptNo < request.getMaximumAttempts();

        return PlaybackDecisionVO.builder()
                .requestId(req.getRequestId()).decisionType(nextRoute.getRouteType())
                .targetUrl(targetUrl).attemptNo(nextAttemptNo).hasNext(hasNext).build();
    }

    private PlaybackDecisionVO buildExistingDecision(PlaybackRequest request, PlaybackAttempt attempt) {
        return PlaybackDecisionVO.builder()
                .requestId(request.getRequestNo()).decisionType("DUPLICATE")
                .attemptNo(attempt.getAttemptNo()).hasNext(false)
                .message("Result already reported").build();
    }

    private PlaybackRoute selectRoute(List<PlaybackRoute> routes, String strategy) {
        if (routes.isEmpty()) throw new BusinessException(ErrorCode.PLAYBACK_ROUTE_UNAVAILABLE);
        return routes.get(0); // PRIORITY default: first route
    }
}
