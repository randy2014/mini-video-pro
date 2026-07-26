package com.video.entitlement.module.user.controller;

import com.video.entitlement.common.response.ApiResponse;
import com.video.entitlement.module.user.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client/invite")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    @GetMapping("/code")
    public ApiResponse<InviteService.InviteStats> getCode(@RequestAttribute("userId") Long userId) {
        return ApiResponse.success(inviteService.getOrCreateCodeAndStats(userId));
    }

    @PostMapping("/activate")
    public ApiResponse<?> activate(@RequestParam String code, @RequestAttribute("userId") Long userId) {
        inviteService.activateInvite(code, userId);
        return ApiResponse.success("激活成功");
    }
}
