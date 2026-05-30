package com.example.repository

import com.example.model.*
import kotlin.math.roundToInt

/**
 * Topic Similarity & Interest Scoring content recommendation engine for SocialDash.
 */
object RecommendationEngine {

    // --- Mock Pool of Viewable Content Items ---
    
    val mockPool = listOf(
        // The requested example recommendations
        ContentItem(
            id = "rec_python_api",
            title = "Python API Guide",
            description = "A comprehensive handbook for designing RESTful endpoints with modern Python paradigms.",
            platform = Platform.YOUTUBE,
            topics = listOf("Python", "API", "Backend")
        ),
        ContentItem(
            id = "rec_backend_arch",
            title = "Backend Architecture Post",
            description = "Deep dive into microservices coordination, database scaling, and distributed architecture patterns.",
            platform = Platform.X,
            topics = listOf("Backend", "Architecture", "System Design")
        ),
        ContentItem(
            id = "rec_async_tutorial",
            title = "Async Programming Tutorial",
            description = "Mastering concurrent asyncio event loops, coroutines, and task workers to maximize Python throughput.",
            platform = Platform.YOUTUBE,
            topics = listOf("Python", "Async", "Concurrency", "Backend")
        ),
        
        // Other general topics in the system
        ContentItem(
            id = "rec_kmp_deep",
            title = "Introduction to Kotlin Multiplatform (KMP)",
            description = "Learn how to share business logic across iOS and Android with single-codebase stability.",
            platform = Platform.YOUTUBE,
            topics = listOf("Kotlin", "Mobile", "Multiplatform", "Android")
        ),
        ContentItem(
            id = "rec_compose_anim",
            title = "Jetpack Compose Animation Guide",
            description = "Create sleek transitions, spring animations, and responsive gesture-based UI effects.",
            platform = Platform.INSTAGRAM,
            topics = listOf("Android", "Compose", "UI", "Design")
        ),
        ContentItem(
            id = "rec_defi_101",
            title = "DeFi Yield Farming 101",
            description = "Understanding liquidity pools, automated market makers (AMMs), and decentralized finance mechanics.",
            platform = Platform.X,
            topics = listOf("DeFi", "Crypto", "Finance")
        ),
        ContentItem(
            id = "rec_sleep_opt",
            title = "Sleep Science and Caffeine Cycles",
            description = "Science-backed strategies for sleep timing, light filtering, and optimizing daily circadian curves.",
            platform = Platform.X,
            topics = listOf("Sleep", "Health", "Longevity", "Performance")
        ),
        ContentItem(
            id = "rec_fasting_rules",
            title = "Intermittent Fasting & Cellular Autophagy",
            description = "A visual exploration of feeding windows, glycemic thresholds, and energy restoration cycles.",
            platform = Platform.INSTAGRAM,
            topics = listOf("Fasting", "Health", "Nutrition")
        )
    )

    // --- Sample Viewed Items for the user to select from in the UI ---
    
    val sampleViewedItems = listOf(
        ContentItem(
            id = "view_fastapi",
            title = "FastAPI Tutorial",
            description = "Learn how to build high-performance Python APIs using FastAPI with automatic OpenAPI docs.",
            platform = Platform.YOUTUBE,
            topics = listOf("Python", "Backend", "FastAPI")
        ),
        ContentItem(
            id = "view_compose",
            title = "Jetpack Compose UI Layouts",
            description = "Building resilient modern interfaces with Row, Column, Box, and Edge-To-Edge scaffolding.",
            platform = Platform.YOUTUBE,
            topics = listOf("Android", "Compose", "UI")
        ),
        ContentItem(
            id = "view_crypto",
            title = "DeFi Liquidity Pools Explained",
            description = "Analyzing smart contracts, token conversions, and slippage calculations in cryptocurrency.",
            platform = Platform.X,
            topics = listOf("Crypto", "DeFi", "Finance")
        ),
        ContentItem(
            id = "view_sleep",
            title = "Sleep Optimization Habits",
            description = "Deep dive on temperature control, melatonin triggers, and sleep scoring indexes.",
            platform = Platform.INSTAGRAM,
            topics = listOf("Sleep", "Health", "Performance")
        )
    )

    // --- Core Recommendation Algorithms ---

    /**
     * Topic Matching Logic:
     * Calculates semantic similarity based on the Jaccard index (intersection over union)
     * of topic tags. Case-insensitive and trimmed.
     * Returns a score between 0.0f and 1.0f.
     */
    fun calculateTopicSimilarity(itemA: ContentItem, itemB: ContentItem): Float {
        val topicsA = itemA.topics.map { it.trim().lowercase() }.toSet()
        val topicsB = itemB.topics.map { it.trim().lowercase() }.toSet()
        
        if (topicsA.isEmpty() || topicsB.isEmpty()) return 0f
        
        val intersectionSize = topicsA.intersect(topicsB).size.toFloat()
        val unionSize = topicsA.union(topicsB).size.toFloat()
        
        return intersectionSize / unionSize
    }

    /**
     * Interest Scoring Logic:
     * Calculates how well a content item fits the user's active interest preferences.
     * Evaluates the average preference weight of matching topics, scaled by the coverage ratio
     * of matches in the item.
     * Returns a score between 0.0f and 1.0f.
     */
    fun calculateInterestScore(item: ContentItem, userInterests: Map<String, Float>): Float {
        val itemTopicsLower = item.topics.map { it.trim().lowercase() }
        
        if (itemTopicsLower.isEmpty() || userInterests.isEmpty()) return 0f
        
        var matchCount = 0
        var totalWeight = 0f
        
        // Normalize user interests keys to lowercase for robust matching
        val normalizedInterests = userInterests.mapKeys { it.key.trim().lowercase() }
        
        for (topic in itemTopicsLower) {
            val weight = normalizedInterests[topic]
            if (weight != null) {
                totalWeight += weight
                matchCount++
            }
        }
        
        if (matchCount == 0) return 0f
        
        val averageWeight = totalWeight / matchCount
        val coverageRatio = matchCount.toFloat() / itemTopicsLower.size
        
        // Blends both how strong the overlapping interests are AND how covered the item is by interests
        return averageWeight * coverageRatio
    }

    /**
     * Generates a ranked list of recommended similar content based on both
     * topic similarity (weight: 60%) and user interest profiles (weight: 40%).
     * Automatically filters out the viewed item itself if present in the pool.
     */
    fun recommend(
        viewedItem: ContentItem,
        userInterests: Map<String, Float>,
        limit: Int = 3
    ): List<RecommendationResult> {
        val results = mutableListOf<RecommendationResult>()
        
        // Standardize viewed topics list
        val viewedTopicsLower = viewedItem.topics.map { it.trim().lowercase() }.toSet()
        
        for (candidate in mockPool) {
            // Cannot recommend the viewed content itself
            if (candidate.id == viewedItem.id || candidate.title.equals(viewedItem.title, ignoreCase = true)) {
                continue
            }
            
            val similarity = calculateTopicSimilarity(viewedItem, candidate)
            val interestScore = calculateInterestScore(candidate, userInterests)
            
            // Core Formula: Combined Weighted Score
            val finalScore = (similarity * 0.6f) + (interestScore * 0.4f)
            
            // Extract the matching intersection of topics
            val candidateTopics = candidate.topics.map { it.trim() }
            val matching = candidateTopics.filter { it.lowercase() in viewedTopicsLower }
            
            // Build dynamic recommendation reasons
            val similarityPercent = (similarity * 100).roundToInt()
            val reasonText = when {
                similarity >= 0.5f && interestScore >= 0.4f -> {
                    "Highly recommended ($similarityPercent% topic match). Shared tags [${matching.joinToString()}], strongly matching your active interest profile."
                }
                similarity >= 0.4f -> {
                    "Recommended due to strong topic similarities ($similarityPercent% match) in [${matching.joinToString()}]."
                }
                interestScore >= 0.4f -> {
                    "Personalized pick matching your preferred topics, showing high interest alignment."
                }
                else -> {
                    "Discovered via general category similarity with matching tags: [${candidate.topics.firstOrNull() ?: ""}]"
                }
            }
            
            val reasonCode = when {
                similarity >= 0.5f && interestScore >= 0.4f -> "PREMIUM_MATCH"
                similarity >= 0.4f -> "HIGH_SIMILARITY"
                interestScore >= 0.4f -> "INTEREST_ALIGNED"
                else -> "DISCOVERY_TRIAL"
            }
            
            results.add(
                RecommendationResult(
                    item = candidate,
                    similarityScore = similarity,
                    interestScore = interestScore,
                    finalScore = finalScore,
                    matchingTopics = matching,
                    recommendationReason = RecommendationReason(reasonCode, reasonText)
                )
            )
        }
        
        // Sort by final combined score descending and take requested limit
        return results.sortedByDescending { it.finalScore }.take(limit)
    }
}
