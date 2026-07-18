package com.video.entitlement.module.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private List<String> roleCodes;
}
