package com.video.entitlement.module.user.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.common.security.JwtTokenProvider;
import com.video.entitlement.module.device.entity.UserDevice;
import com.video.entitlement.module.device.repository.UserDeviceRepository;
import com.video.entitlement.module.user.dto.*;
import com.video.entitlement.module.user.entity.UserAccount;
import com.video.entitlement.module.user.entity.UserLoginLog;
import com.video.entitlement.module.user.entity.enums.LoginResult;
import com.video.entitlement.module.user.entity.enums.UserStatus;
import com.video.entitlement.module.user.repository.UserAccountRepository;
import com.video.entitlement.module.user.repository.UserLoginLogRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAccountRepository userAccountRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final UserLoginLogRepository userLoginLogRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse login(UserLoginRequest request) {
        // Verify code (simplified: accept any code for dev)
        if (!"000000".equals(request.getVerificationCode())) {
            logLogin(null, request.getDevicePublicId(), LoginResult.FAILED, "Invalid code");
            throw new BusinessException(ErrorCode.AUTH_TOKEN_INVALID, "验证码错误");
        }
        UserAccount user = userAccountRepository.findByMobile(request.getMobile())
                .orElseGet(() -> createUser(request.getMobile()));

        if (UserStatus.DISABLED.getCode().equals(user.getStatus())) {
            logLogin(user.getId(), request.getDevicePublicId(), LoginResult.BLOCKED, "User disabled");
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // Bind device
        bindDevice(user.getId(), request.getDevicePublicId(), request.getClientType(), request.getAppVersion());

        // Update login info
        user.setLastLoginAt(LocalDateTime.now());
        userAccountRepository.save(user);

        logLogin(user.getId(), request.getDevicePublicId(), LoginResult.SUCCESS, null);

        String role = user.getRiskLevel() != null && "HIGH".equals(user.getRiskLevel()) ? "USER_RESTRICTED" : "USER";
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUserNo(), role, null);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getUserNo(), role);

        return AuthResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .user(UserVO.builder().userNo(user.getUserNo()).nickname(user.getNickname())
                        .mobile(user.getMobile()).status(user.getStatus()).riskLevel(user.getRiskLevel()).build())
                .expiresIn(jwtTokenProvider.getAccessExpirationMs() / 1000).build();
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        Claims claims;
        try {
            claims = jwtTokenProvider.parseRefreshToken(request.getRefreshToken());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED);
        }
        Long userId = Long.parseLong(claims.getSubject());
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (UserStatus.DISABLED.getCode().equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        String role = claims.get("role", String.class);
        String accessToken = jwtTokenProvider.generateAccessToken(userId, user.getUserNo(), role, null);
        return AuthResponse.builder().accessToken(accessToken).refreshToken(request.getRefreshToken())
                .user(UserVO.builder().userNo(user.getUserNo()).nickname(user.getNickname())
                        .mobile(user.getMobile()).status(user.getStatus()).riskLevel(user.getRiskLevel()).build())
                .expiresIn(jwtTokenProvider.getAccessExpirationMs() / 1000).build();
    }

    private UserAccount createUser(String mobile) {
        String userNo = "U" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        UserAccount user = UserAccount.builder()
                .userNo(userNo).mobile(mobile)
                .nickname("用户" + mobile.substring(Math.max(0, mobile.length() - 4)))
                .status(UserStatus.ACTIVE.getCode())
                .riskLevel("LOW").build();
        return userAccountRepository.save(user);
    }

    private void bindDevice(Long userId, String devicePublicId, String clientType, String appVersion) {
        Optional<UserDevice> existing = userDeviceRepository.findByUserIdAndDevicePublicId(userId, devicePublicId);
        if (existing.isPresent()) {
            UserDevice dev = existing.get();
            dev.setLastActiveAt(LocalDateTime.now());
            dev.setAppVersion(appVersion);
            userDeviceRepository.save(dev);
        } else {
            UserDevice dev = UserDevice.builder()
                    .userId(userId).devicePublicId(devicePublicId)
                    .clientType(clientType).status("ACTIVE")
                    .appVersion(appVersion).lastActiveAt(LocalDateTime.now()).build();
            userDeviceRepository.save(dev);
        }
    }

    private void logLogin(Long userId, String deviceId, LoginResult result, String reason) {
        UserLoginLog logEntry = UserLoginLog.builder()
                .userId(userId).devicePublicId(deviceId)
                .loginResult(result.getCode()).failureReason(reason).build();
        userLoginLogRepository.save(logEntry);
    }
}
