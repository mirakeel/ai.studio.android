package com.example.model

/**
 * Represents a piece of content that can be viewed and recommended within SocialDash.
 */
data class ContentItem(
    val id: String,
    val title: String,
    val description: String,
    val platform: Platform,
    val topics: List<String>
)

/**
 * Represents the detailed results of scoring and ranking content similarity and interest match.
 */
data class RecommendationResult(
    val item: ContentItem,
    val similarityScore: Float,     // Similarity to the viewed item (0.0 to 1.0)
    val interestScore: Float,       // Alignment with user's preferred interest weights (0.0 to 1.0)
    val finalScore: Float,          // Combined weighted formula score (0.0 to 1.0)
    val matchingTopics: List<String>,
    val recommendationReason: RecommendationReason
)
