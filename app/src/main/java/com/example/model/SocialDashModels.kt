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

enum class IntelCategory {
    TECHNOLOGY, LIFESTYLE, BUSINESS, CREATIVE, FINANCE, EDUCATION, ENTERTAINMENT
}

data class MentionedAccount(
    val handle: String,
    val displayName: String,
    val followerCount: String,
    val avatarColorSeed: String = ""
)

data class TopicSummary(
    val mainConcept: String,
    val summaryText: String,
    val keyBulletPoints: List<String> = emptyList()
)

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
    val velocity: Double,
    // AI Intel additions
    val topicSummary: TopicSummary? = null,
    val whyTrending: String = "",
    val mentionedAccounts: List<MentionedAccount> = emptyList(),
    val contentExamples: List<String> = emptyList(),
    val intelCategory: IntelCategory = IntelCategory.TECHNOLOGY
)

data class PlatformIntel(
    val platform: Platform,
    val overallSentiment: String,
    val volumeTrend: String,
    val topCategory: IntelCategory,
    val trendingTopics: List<TrendingTopic> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)

data class UsageStats(
    val platform: Platform,
    val dailyImpressions: Long,
    val engagementRate: Double,
    val minutesToday: Int = 0,
    val minutesWeek: Int = 0,
    val minutesMonth: Int = 0
)

data class UserSettings(
    val aiDigestsEnabled: Map<Platform, Boolean>,
    val notificationStyle: NotificationStyle,
    val syncFrequencyMinutes: Int,
    val morningBriefingTime: String, // HH:mm format
    val interestTopics: Set<String>,
    val activityFilters: Set<ActivityFilter>
)

data class PlatformAnalysis(
    val platform: Platform,
    val analysisText: String,
    val timestamp: Long
)
