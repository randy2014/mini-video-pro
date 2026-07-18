package com.video.entitlement.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "success",
    val data: T? = null,
    val requestId: String = "",
    val timestamp: Long = 0
)

// Auth
data class AdminLoginRequest(val username: String, val password: String)
data class AdminLoginResponse(val adminToken: String, val username: String, val permissions: List<String>)

data class UserLoginRequest(
    val mobile: String,
    val verificationCode: String,
    val devicePublicId: String,
    val clientType: String = "ANDROID",
    val appVersion: String = "1.0.0"
)
data class RefreshTokenRequest(val refreshToken: String, val devicePublicId: String)
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserVO,
    val expiresIn: Long
)
data class UserVO(val userNo: String, val nickname: String, val mobile: String, val status: String, val riskLevel: String)

// Platform
data class VideoPlatform(
    val platformCode: String, val platformName: String,
    val homeUrl: String, val status: String, val domains: List<String>?
)

// URL
data class UrlStandardizeRequest(val url: String)
data class UrlStandardizeResponse(
    val canonicalUrl: String, val contentKey: String,
    val platformCode: String?, val matched: Boolean
)

// Entitlement
data class UserEntitlementVO(
    val id: Long, val productCode: String?, val productName: String?,
    val status: String, val sourceType: String,
    val effectiveAt: String?, val expiresAt: String?, val usedTotal: Int
)
data class RedeemRequest(val code: String, val devicePublicId: String)
data class RedeemResponse(val userEntitlement: UserEntitlementVO)

// Playback
data class PlaybackResolveRequest(
    val platformCode: String, val contentKey: String,
    val canonicalUrl: String?, val originalUrl: String?
)
data class PlaybackDecisionVO(
    val requestId: String, val decisionType: String,
    val targetUrl: String?, val attemptNo: Int?,
    val hasNext: Boolean?, val message: String?
)
data class PlaybackReportRequest(
    val requestId: String, val attemptNo: Int,
    val result: String, val errorType: String?, val durationMs: Long?
)
