package com.video.entitlement.module.admin.controller;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.response.PageResult;
import com.video.entitlement.module.user.entity.UserAccount;
import com.video.entitlement.module.user.entity.enums.UserStatus;
import com.video.entitlement.module.user.repository.UserAccountRepository;
import com.video.entitlement.module.entitlement.repository.UserEntitlementRepository;
import com.video.entitlement.module.device.repository.UserDeviceRepository;
import com.video.entitlement.module.user.repository.UserLoginLogRepository;
import com.video.entitlement.module.user.repository.InviteCodeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/admin/api/v1/users")
@RequiredArgsConstructor
public class UserManageController {

    private final UserAccountRepository userAccountRepository;
    private final UserEntitlementRepository userEntitlementRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final UserLoginLogRepository userLoginLogRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "用户列表")
    @GetMapping
    public ApiResponse<PageResult<?>> list(@RequestParam(required = false) String keyword, Pageable pageable) {
        Page<UserAccount> page;
        if (keyword != null && !keyword.isBlank()) {
            page = userAccountRepository.findByMobileContaining(keyword, pageable);
        } else {
            page = userAccountRepository.findAll(pageable);
        }
        // 查询每个用户的权益信息
        List<Long> userIds = page.getContent().stream().map(UserAccount::getId).toList();
        var entitlementsMap = userEntitlementRepository.findByUserIdIn(userIds)
                .stream()
                .collect(Collectors.groupingBy(
                    ue -> ue.getUserId(),
                    Collectors.mapping(ue -> Map.of(
                        "entitlementCode", ue.getEntitlementCode() != null ? ue.getEntitlementCode() : "",
                        "expireTime", ue.getExpireTime() != null ? ue.getExpireTime().toString() : ""
                    ), Collectors.toList())
                ));

        List<Map<String, Object>> rows = page.getContent().stream().map(user -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", user.getId());
            m.put("userNo", user.getUserNo());
            m.put("mobile", user.getMobile());
            m.put("nickname", user.getNickname());
            m.put("status", user.getStatus());
            m.put("riskLevel", user.getRiskLevel());
            m.put("lastLoginAt", user.getLastLoginAt());
            m.put("createdAt", user.getCreatedAt());
            m.put("entitlements", entitlementsMap.getOrDefault(user.getId(), List.of()));
            return m;
        }).toList();

        return ApiResponse.success(PageResult.of(
                rows, page.getTotalElements(),
                page.getNumber() + 1, page.getSize()));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public ApiResponse<?> get(@PathVariable Long id) {
        return ApiResponse.success(userAccountRepository.findById(id));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(status);
        userAccountRepository.save(user);
        return ApiResponse.success(null);
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/reset-password")
    public ApiResponse<?> resetPassword(@PathVariable Long id) {
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode("00000000"));
        userAccountRepository.save(user);
        return ApiResponse.success(null);
    }

    @Operation(summary = "删除用户（物理删除，同时清理关联数据）")
    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ApiResponse<?> delete(@PathVariable Long id) {
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        // 清理所有关联数据
        userEntitlementRepository.deleteByUserId(id);
        userDeviceRepository.deleteByUserId(id);
        userLoginLogRepository.deleteByUserId(id);
        inviteCodeRepository.deleteByUserId(id);
        userAccountRepository.delete(user);
        return ApiResponse.success(null);
    }
}
