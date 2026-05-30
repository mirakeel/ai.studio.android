package com.example.api

import com.example.api.dto.*
import retrofit2.http.*

interface SocialDashService {
    @GET("platforms")
    suspend fun getPlatforms(): List<PlatformInfoDTO>

    @GET("trending")
    suspend fun getTrendingTopics(): List<TrendingTopicDTO>

    @GET("usage")
    suspend fun getUsageStats(): List<UsageStatsDTO>

    @GET("settings")
    suspend fun getSettings(): UserSettingsDTO

    @PATCH("settings")
    suspend fun updateSettings(@Body settings: UserSettingsDTO): UserSettingsDTO

    @GET("digests/{platform}")
    suspend fun getLatestDigest(@Path("platform") platform: String): DigestDTO

    @POST("analysis/{platform}")
    suspend fun generateAnalysis(@Path("platform") platform: String): AnalysisResponseDTO

    @POST("tracker/heartbeat")
    suspend fun sendHeartbeat(@Body request: HeartbeatRequestDTO)

    @GET("tracker/stats")
    suspend fun getTrackerStats(): List<UsageStatsDTO>
}
