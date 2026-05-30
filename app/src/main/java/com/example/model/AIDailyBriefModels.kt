package com.example.model

data class PlatformSummary(
    val summaryText: String,
    val keyEvents: List<String>,
    val topDiscussions: List<String>,
    val recommendedTopics: List<String>
)

data class AIDailyBrief(
    val instagramBrief: PlatformSummary,
    val xBrief: PlatformSummary,
    val youtubeBrief: PlatformSummary,
    val generatedAt: Long = System.currentTimeMillis()
)
