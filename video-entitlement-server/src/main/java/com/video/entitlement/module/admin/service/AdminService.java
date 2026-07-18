package com.video.entitlement.module.admin.service;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.common.security.JwtTokenProvider;
import com.video.entitlement.module.admin.dto.*;
import com.video.entitlement.module.admin.entity.*;
import com.video.entitlement.module.admin.entity.enums.AdminStatus;
import com.video.entitlement.module.admin.repository.*;
import com.video.entitlement.module.admin.entity.AdminOperationLog;
import com.video.entitlement.module.admin.repository.AdminOperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminUserRepository adminUserRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final AdminPermissionRepository adminPermissionRepository;
    private final AdminUserRoleRepository adminUserRoleRepository;
    private final AdminRolePermissionRepository adminRolePermissionRepository;
    private final AdminOperationLogRepository adminOperationLogRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-username:admin}")
    private String defaultUsername;

    @Value("${app.admin.default-password:Admin@123}")
    private String defaultPassword;

    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_CREDENTIALS_INVALID));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTH_CREDENTIALS_INVALID);
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        List<String> permissions = getPermissions(user.getId());
        String token = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), "ADMIN",
                Map.of("permissions", String.join(",", permissions)));
        return AdminLoginResponse.builder()
                .adminToken(token).username(user.getUsername()).permissions(permissions).build();
    }

    private List<String> getPermissions(Long adminUserId) {
        List<AdminUserRole> userRoles = adminUserRoleRepository.findByAdminUserId(adminUserId);
        Set<String> permSet = new HashSet<>();
        for (AdminUserRole ur : userRoles) {
            List<AdminRolePermission> rpList = adminRolePermissionRepository.findByRoleId(ur.getRoleId());
            for (AdminRolePermission rp : rpList) {
                adminPermissionRepository.findById(rp.getPermissionId())
                        .ifPresent(p -> permSet.add(p.getPermissionCode()));
            }
        }
        return new ArrayList<>(permSet);
    }

    @Transactional
    public AdminVO createAdmin(AdminCreateRequest request) {
        if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "用户名已存在");
        }
        AdminUser user = AdminUser.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE").build();
        user = adminUserRepository.save(user);
        if (request.getRoleCodes() != null) {
            for (String roleCode : request.getRoleCodes()) {
                AdminRole role = adminRoleRepository.findByRoleCode(roleCode)
                        .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, "角色不存在: " + roleCode));
                adminUserRoleRepository.save(AdminUserRole.builder()
                        .adminUserId(user.getId()).roleId(role.getId()).build());
            }
        }
        return toVO(user);
    }

    private AdminVO toVO(AdminUser user) {
        return AdminVO.builder().id(user.getId()).username(user.getUsername())
                .status(user.getStatus()).lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt()).build();
    }

    @Transactional
    public void initDefaultAdmin() {
        if (adminUserRepository.findByUsername(defaultUsername).isEmpty()) {
            AdminUser admin = AdminUser.builder()
                    .username(defaultUsername)
                    .passwordHash(passwordEncoder.encode(defaultPassword))
                    .status("ACTIVE").build();
            admin = adminUserRepository.save(admin);
            // Create default roles
            if (adminRoleRepository.findByRoleCode("ADMIN").isEmpty()) {
                AdminRole role = adminRoleRepository.save(AdminRole.builder().roleCode("ADMIN").roleName("超级管理员").status("ACTIVE").build());
                adminUserRoleRepository.save(AdminUserRole.builder().adminUserId(admin.getId()).roleId(role.getId()).build());
            }
            log.info("Default admin '{}' initialized", defaultUsername);
        }
    }
}
