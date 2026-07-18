package com.video.entitlement.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private String userNo;
    private String nickname;
    private String mobile;
    private String status;
    private String riskLevel;
}
