package com.example.model

enum class Platform {
    INSTAGRAM, X, YOUTUBE
}

enum class NotificationStyle {
    POPUP, IN_APP, BOTH, ON_OPEN
}

enum class ActivityFilter {
    POSTS, LIKES, COMMENTS, REPOSTS
}

data class PlatformInfo(
    val platform: Platform,
    val displayName: String,
    val isConnected: Boolean
)

data class Digest(
    val id: String,
    val platform: Platform,
    val content: String,
    val timestamp: Long
)

data class TrendingTopic(
    val id: String,
    val platform: Platform,
    val title: String,
    val impressions: Long,
    val velocity: Double
)

data class UsageStats(
    val platform: Platform,
    val dailyImpressions: Long,
    val engagementRate: Double
)

data class UserSettings(
    val aiDigestsEnabled: Map<Platform, Boolean>,
    val notificationStyle: NotificationStyle,
    val syncFrequencyMinutes: Int,
    val morningBriefingTime: String, // HH:mm format
    val interestTopics: Set<String>,
    val activityFilters: Set<ActivityFilter>
)
