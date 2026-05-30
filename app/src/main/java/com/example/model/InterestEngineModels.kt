package com.example.model

/**
 * Represents a high-level category of content (e.g., Tech, Finance, Gaming).
 */
data class ContentCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String
)

/**
 * Represents a specific visual or semantic interest topic associated with a ContentCategory.
 */
data class InterestTopic(
    val id: String,
    val name: String,
    val category: ContentCategory,
    val baseWeight: Float = 0.5f
)

/**
 * Explains why a specific recommendation was made to the user.
 */
data class RecommendationReason(
    val code: String, // e.g., "HIGH_ENGAGEMENT", "MATCHES_INTEREST", "TRENDING_NOW"
    val description: String
)

/**
 * Represents structured related content recommended across platform feeds.
 */
data class RelatedContent(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val platform: Platform,
    val category: ContentCategory,
    val topics: List<InterestTopic>,
    val relevanceScore: Float, // Higher values mean more relevant (0.0 to 1.0)
    val publishedTimeMillis: Long,
    val recommendationReason: RecommendationReason
)

/**
 * Represents the profile containing user interests and view metrics.
 */
data class UserInterestProfile(
    val userId: String,
    val activeInterests: Map<String, Float>, // Topic ID -> interest weight (0.0 to 1.0)
    val viewedTopicHistory: List<String>,   // Ordered list of viewed Topic IDs
    val lastUpdatedMillis: Long
)
