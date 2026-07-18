package com.video.entitlement.module.admin.controller;

import com.video.entitlement.common.exception.BusinessException;
import com.video.entitlement.common.exception.ErrorCode;
import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.common.response.PageResult;
import com.video.entitlement.module.user.entity.UserAccount;
import com.video.entitlement.module.user.entity.enums.UserStatus;
import com.video.entitlement.module.user.repository.UserAccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/admin/api/v1/users")
@RequiredArgsConstructor
public class UserManageController {

    private final UserAccountRepository userAccountRepository;

    @Operation(summary = "用户列表")
    @GetMapping
    public ApiResponse<PageResult<?>> list(@RequestParam(required = false) String keyword, Pageable pageable) {
        Page<UserAccount> page;
        if (keyword != null && !keyword.isBlank()) {
            page = userAccountRepository.findByMobileContaining(keyword, pageable);
        } else {
            page = userAccountRepository.findAll(pageable);
        }
        return ApiResponse.success(PageResult.of(
                page.getContent(), page.getTotalElements(),
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
}
