package com.video.entitlement.common.exception;

import com.video.entitlement.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e, HttpServletRequest request) {
        log.warn("Business exception: [{}] {}", e.getCode(), e.getMessage());
        HttpStatus status = resolveHttpStatus(e.getCode());
        return ResponseEntity.status(status)
                .body(ApiResponse.error(e.getCode(), e.getMessage(), getRequestId(request)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), msg, getRequestId(request)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.ADMIN_FORBIDDEN.getCode(), "权限不足", getRequestId(request)));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.AUTH_TOKEN_INVALID.getCode(), "认证失败", getRequestId(request)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e, HttpServletRequest request) {
        log.error("Unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.SYSTEM_ERROR.getCode(),
                        ErrorCode.SYSTEM_ERROR.getMessage(), getRequestId(request)));
    }

    private HttpStatus resolveHttpStatus(int code) {
        return switch (code / 1000) {
            case 1 -> HttpStatus.UNAUTHORIZED;
            case 2, 3, 4, 11, 12 -> HttpStatus.FORBIDDEN;
            case 6, 7, 8, 9 -> {
                if (code == ErrorCode.RATE_LIMITED.getCode() || code == ErrorCode.ENTITLEMENT_DAILY_LIMIT.getCode()
                    || code == ErrorCode.PLAYBACK_REPORT_DUPLICATED.getCode() || code == ErrorCode.PLAYBACK_MAX_ATTEMPTS.getCode()
                    || code == ErrorCode.ENTITLEMENT_CODE_USED.getCode() || code == ErrorCode.INVALID_STATUS_TRANSITION.getCode()
                    || code == ErrorCode.CONFIG_VERSION_INVALID.getCode()) {
                    yield HttpStatus.CONFLICT;
                }
                yield HttpStatus.SERVICE_UNAVAILABLE;
            }
            case 5 -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private String getRequestId(HttpServletRequest request) {
        return request.getHeader("X-Request-Id");
    }
}
