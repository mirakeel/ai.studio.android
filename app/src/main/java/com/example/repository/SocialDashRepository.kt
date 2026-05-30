package com.example.repository

import com.example.api.NetworkProvider
import com.example.api.SocialDashService
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SocialDashRepository(
    private val service: SocialDashService = NetworkProvider.socialDashService
) {
    private val _settings = MutableStateFlow(MockData.getMockSettings())
    val settings = _settings.asStateFlow()

    suspend fun fetchSettings() {
        try {
            val settingsDto = service.getSettings()
            _settings.update { settingsDto.toDomain() }
        } catch (e: Exception) {
            // Handle error (log it, etc.)
        }
    }

    suspend fun getUsageStats(): List<UsageStats> {
        return try {
            service.getUsageStats().map { it.toDomain() }
        } catch (e: Exception) {
            MockData.getMockUsageStats()
        }
    }

    suspend fun getTrendingTopics(): List<TrendingTopic> {
        return try {
            service.getTrendingTopics().map { it.toDomain() }
        } catch (e: Exception) {
            MockData.getMockTrendingTopics()
        }
    }

    suspend fun getPlatformInfo(): List<PlatformInfo> {
        return try {
            service.getPlatforms().map { it.toDomain() }
        } catch (e: Exception) {
            MockData.platforms
        }
    }

    suspend fun getLatestDigest(platform: String): Digest? {
        return try {
            service.getLatestDigest(platform).toDomain()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateAnalysis(platform: String): PlatformAnalysis? {
        return try {
            service.generateAnalysis(platform).toDomain()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendHeartbeat(platform: String, durationSeconds: Int) {
        try {
            service.sendHeartbeat(com.example.api.dto.HeartbeatRequestDTO(platform, durationSeconds))
        } catch (e: Exception) {
            // Silence heartbeat errors
        }
    }

    suspend fun getTrackerStats(): List<UsageStats> {
        return try {
            service.getTrackerStats().map { it.toDomain() }
        } catch (e: Exception) {
            getUsageStats() // Fallback to basic usage
        }
    }

    suspend fun updateSettings(newSettings: UserSettings) {
        try {
            val updatedDto = service.updateSettings(newSettings.toDTO())
            _settings.update { updatedDto.toDomain() }
        } catch (e: Exception) {
            // Optimistic update fallback or error handling
            _settings.update { newSettings }
        }
    }
}
