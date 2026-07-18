package com.video.entitlement.data.repository

import com.video.entitlement.data.api.RetrofitClient
import com.video.entitlement.data.api.ApiService
import com.video.entitlement.data.model.*

class EntitleRepository {
    private val api: ApiService = RetrofitClient.apiService

    suspend fun getPlatforms() = api.getPlatforms()
    suspend fun standardizeUrl(req: UrlStandardizeRequest) = api.standardizeUrl(req)
    suspend fun userLogin(req: UserLoginRequest) = api.userLogin(req)
    suspend fun refreshToken(req: RefreshTokenRequest) = api.refreshToken(req)
    suspend fun getMyEntitlements() = api.getMyEntitlements()
    suspend fun redeemCode(req: RedeemRequest) = api.redeemCode(req)
    suspend fun resolvePlayback(req: PlaybackResolveRequest) = api.resolvePlayback(req)
    suspend fun reportPlayback(req: PlaybackReportRequest) = api.reportPlayback(req)
}
