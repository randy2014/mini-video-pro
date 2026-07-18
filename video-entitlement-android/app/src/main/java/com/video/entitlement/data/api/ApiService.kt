package com.video.entitlement.data.api

import com.video.entitlement.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // User
    @POST("api/v1/auth/login")
    suspend fun userLogin(@Body req: UserLoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body req: RefreshTokenRequest): Response<ApiResponse<AuthResponse>>

    // Platform
    @GET("api/v1/client/platforms")
    suspend fun getPlatforms(): Response<ApiResponse<List<VideoPlatform>>>

    @POST("api/v1/client/url/standardize")
    suspend fun standardizeUrl(@Body req: UrlStandardizeRequest): Response<ApiResponse<UrlStandardizeResponse>>

    // Entitlement
    @GET("api/v1/entitlement/my")
    suspend fun getMyEntitlements(): Response<ApiResponse<List<UserEntitlementVO>>>

    @POST("api/v1/entitlement/redeem")
    suspend fun redeemCode(@Body req: RedeemRequest): Response<ApiResponse<RedeemResponse>>

    // Playback
    @POST("api/v1/playback/resolve")
    suspend fun resolvePlayback(@Body req: PlaybackResolveRequest): Response<ApiResponse<PlaybackDecisionVO>>

    @POST("api/v1/playback/report")
    suspend fun reportPlayback(@Body req: PlaybackReportRequest): Response<ApiResponse<PlaybackDecisionVO>>
}
