package com.example.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlatformInfoDTO(
    @field:Json(name = "platform") val platform: String,
    @field:Json(name = "display_name") val displayName: String,
    @field:Json(name = "is_connected") val isConnected: Boolean
)

@JsonClass(generateAdapter = true)
data class DigestDTO(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "platform") val platform: String,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "timestamp") val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class TrendingTopicDTO(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "platform") val platform: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "impressions") val impressions: Long,
    @field:Json(name = "velocity") val velocity: Double
)

@JsonClass(generateAdapter = true)
data class UsageStatsDTO(
    @field:Json(name = "platform") val platform: String,
    @field:Json(name = "daily_impressions") val dailyImpressions: Long,
    @field:Json(name = "engagement_rate") val engagementRate: Double,
    @field:Json(name = "minutes_today") val minutesToday: Int = 0,
    @field:Json(name = "minutes_week") val minutesWeek: Int = 0,
    @field:Json(name = "minutes_month") val minutesMonth: Int = 0
)

@JsonClass(generateAdapter = true)
data class HeartbeatRequestDTO(
    @field:Json(name = "platform") val platform: String,
    @field:Json(name = "duration_seconds") val durationSeconds: Int = 60
)

@JsonClass(generateAdapter = true)
data class UserSettingsDTO(
    @field:Json(name = "ai_digests_enabled") val aiDigestsEnabled: Map<String, Boolean>,
    @field:Json(name = "notification_style") val notificationStyle: String,
    @field:Json(name = "sync_frequency_minutes") val syncFrequencyMinutes: Int,
    @field:Json(name = "morning_briefing_time") val morningBriefingTime: String,
    @field:Json(name = "interest_topics") val interestTopics: List<String>,
    @field:Json(name = "activity_filters") val activityFilters: List<String>
)

@JsonClass(generateAdapter = true)
data class AnalysisResponseDTO(
    @field:Json(name = "platform") val platform: String,
    @field:Json(name = "analysis_text") val analysisText: String,
    @field:Json(name = "timestamp") val timestamp: Long
)
