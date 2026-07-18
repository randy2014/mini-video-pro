package com.video.entitlement.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private int code = 0;
    @Builder.Default
    private String message = "success";
    private T data;
    @Builder.Default
    private String requestId = "";
    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().code(0).message("success").data(data).build();
    }

    public static <T> ApiResponse<T> success(T data, String requestId) {
        return ApiResponse.<T>builder().code(0).message("success").data(data).requestId(requestId).build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder().code(code).message(message).build();
    }

    public static <T> ApiResponse<T> error(int code, String message, String requestId) {
        return ApiResponse.<T>builder().code(code).message(message).requestId(requestId).build();
    }
}
