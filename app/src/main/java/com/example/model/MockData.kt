package com.example.model

object MockData {

    val platforms = listOf(
        PlatformInfo(Platform.INSTAGRAM, "Instagram", true),
        PlatformInfo(Platform.X, "X", true),
        PlatformInfo(Platform.YOUTUBE, "YouTube", true)
    )

    fun getMockDigest(platform: Platform) = Digest(
        id = "d1",
        platform = platform,
        content = "Your ${platform.name} performance is up 15%.",
        timestamp = System.currentTimeMillis()
    )

    fun getMockTrendingTopics() = listOf(
        TrendingTopic("t1", Platform.X, "#ModernCompose", 2100000, 3.4),
        TrendingTopic("t2", Platform.INSTAGRAM, "reels_algorithm", 890000, 1.85),
        TrendingTopic("t3", Platform.YOUTUBE, "JetpackTips", 1400000, 2.1)
    )

    fun getMockUsageStats() = listOf(
        UsageStats(Platform.INSTAGRAM, 45000, 0.05),
        UsageStats(Platform.X, 32000, 0.08),
        UsageStats(Platform.YOUTUBE, 55000, 0.03)
    )

    fun getMockSettings() = UserSettings(
        aiDigestsEnabled = mapOf(
            Platform.INSTAGRAM to true,
            Platform.X to true,
            Platform.YOUTUBE to true
        ),
        notificationStyle = NotificationStyle.IN_APP,
        syncFrequencyMinutes = 60,
        morningBriefingTime = "08:00",
        interestTopics = setOf("Android", "Compose", "Kotlin"),
        activityFilters = setOf(ActivityFilter.POSTS, ActivityFilter.COMMENTS)
    )
}
