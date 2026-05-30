package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Data layer interface for managing user interest profiles, topic catalogs, and content recommendations.
 */
interface InterestEngineRepository {
    /**
     * Observable stream of the current user's interest profile.
     */
    val userInterestProfile: StateFlow<UserInterestProfile>

    /**
     * Gets the latest interest profile synchronously or as a snapshot.
     */
    suspend fun getInterestProfile(): UserInterestProfile

    /**
     * Retrieves all available content categories in the system.
     */
    suspend fun getAllCategories(): List<ContentCategory>

    /**
     * Retrieves all available interest topics in the system.
     */
    suspend fun getAllTopics(): List<InterestTopic>

    /**
     * Adds a topic to the user's active interests.
     */
    suspend fun addInterest(topicId: String, initialWeight: Float = 0.5f)

    /**
     * Removes a topic from the user's active interests.
     */
    suspend fun removeInterest(topicId: String)

    /**
     * Updates the preference weight of a specific interest.
     */
    suspend fun updateInterestWeight(topicId: String, weight: Float)

    /**
     * Records that the user viewed content belonging to a specific topic, updating user history.
     */
    suspend fun trackViewedTopic(topicId: String)

    /**
     * Generates a ranked list of content recommendations based on active interest weights.
     */
    suspend fun getRecommendations(limit: Int = 10): List<RelatedContent>

    /**
     * Resets the user profile to built-in default values.
     */
    suspend fun resetProfileToDefaults()
}

/**
 * Mock implementation of [InterestEngineRepository] containing seed data and reactive in-memory state tracking.
 */
object MockInterestEngineRepository : InterestEngineRepository {

    // --- Mock Seed Data ---

    val categories = listOf(
        ContentCategory("tech_ai", "Tech & AI", "Artificial Intelligence, KMP, software engineering, and gadgets", "ic_tech"),
        ContentCategory("finance_crypto", "Finance & Crypto", "Investment strategies, stock market, and decentralized currencies", "ic_finance"),
        ContentCategory("gaming", "Gaming World", "Console releases, competitive gaming, and game design", "ic_gaming"),
        ContentCategory("health_biohacking", "Health & Longevity", "Nutrition, rest, functional fitness, and longevity studies", "ic_health"),
        ContentCategory("space_science", "Space & Space Science", "Deep space, quantum physics, fusion, and climate technology", "ic_science")
    )

    val topics = listOf(
        // Tech
        InterestTopic("topic_ai", "Artificial Intelligence", categories[0], 0.8f),
        InterestTopic("topic_kotlin", "Kotlin Multiplatform", categories[0], 0.7f),
        InterestTopic("topic_compose", "Jetpack Compose UI", categories[0], 0.6f),
        InterestTopic("topic_quantum", "Quantum Computing", categories[0], 0.4f),

        // Finance
        InterestTopic("topic_defi", "DeFi Protocols", categories[1], 0.5f),
        InterestTopic("topic_etf", "ETF Investing", categories[1], 0.6f),
        InterestTopic("topic_macro", "Macroeconomics", categories[1], 0.5f),
        InterestTopic("topic_val_investing", "Value Investing", categories[1], 0.7f),

        // Gaming
        InterestTopic("topic_indies", "Indie Game Gems", categories[2], 0.6f),
        InterestTopic("topic_unreal", "Unreal Engine 5", categories[2], 0.5f),
        InterestTopic("topic_esports", "Esports Tournaments", categories[2], 0.4f),

        // Health
        InterestTopic("topic_fasting", "Intermittent Fasting", categories[3], 0.5f),
        InterestTopic("topic_sleep", "Sleep Optimization", categories[3], 0.8f),
        InterestTopic("topic_longevity", "Longevity Medicine", categories[3], 0.7f),

        // Space
        InterestTopic("topic_mars", "Mars Exploration", categories[4], 0.7f),
        InterestTopic("topic_fusion", "Nuclear Fusion", categories[4], 0.5f),
        InterestTopic("topic_webb", "James Webb Discoveries", categories[4], 0.6f)
    )

    private val staticRecommendations = listOf(
        RelatedContent(
            id = "recommend_1",
            title = "The Shift to Jetpack Compose Multiplatform",
            description = "Explore how modern teams are sharing beautiful declarative UI screens seamlessly across iOS and Android.",
            url = "https://youtube.com/watch?v=compose_multiplatform",
            platform = Platform.YOUTUBE,
            category = categories[0],
            topics = listOf(topics[1], topics[2]),
            relevanceScore = 0.95f,
            publishedTimeMillis = System.currentTimeMillis() - 7200000,
            recommendationReason = RecommendationReason("HIGHLY_RELEVANT", "Based on your focus on modern UI layout frameworks.")
        ),
        RelatedContent(
            id = "recommend_2",
            title = "Sleep Mechanics: Reaching Deep Stage 4 Regularly",
            description = "A visual exploration of temperature control, magnesium pairings, and light hygiene for elite recovery.",
            url = "https://instagram.com/reel/sleep_habits",
            platform = Platform.INSTAGRAM,
            category = categories[3],
            topics = listOf(topics[12]),
            relevanceScore = 0.88f,
            publishedTimeMillis = System.currentTimeMillis() - 14400000,
            recommendationReason = RecommendationReason("BEST_SELLER", "Matches your deep interest in biological longevity and rest.")
        ),
        RelatedContent(
            id = "recommend_3",
            title = "Is Fusion Power Closer Than We Think?",
            description = "Breakdown of the newest helical stellerator results proving net tritium recovery efficiency gains.",
            url = "https://x.com/tweets/fusion_power",
            platform = Platform.X,
            category = categories[4],
            topics = listOf(topics[15]),
            relevanceScore = 0.82f,
            publishedTimeMillis = System.currentTimeMillis() - 86400000,
            recommendationReason = RecommendationReason("TRENDING", "Science category trend spiking on the platform.")
        ),
        RelatedContent(
            id = "recommend_4",
            title = "The 101 Guide to Smart ETF Asset Allocations",
            description = "How to mix small-cap value, international aggregates, and treasury bills to create a bulletproof index foundation.",
            url = "https://youtube.com/watch?v=etf_investing",
            platform = Platform.YOUTUBE,
            category = categories[1],
            topics = listOf(topics[5], topics[7]),
            relevanceScore = 0.76f,
            publishedTimeMillis = System.currentTimeMillis() - 172800000,
            recommendationReason = RecommendationReason("FINANCIAL_INTELLIGENCE", "Aligned with your personal finance portfolio tracker.")
        ),
        RelatedContent(
            id = "recommend_5",
            title = "Deep-Dive of AI Agent Architecture: Chains vs Graphs",
            description = "Understanding how cyclic workflows in modern large model systems enable autonomous task correction.",
            url = "https://x.com/tweets/ai_agents",
            platform = Platform.X,
            category = categories[0],
            topics = listOf(topics[0]),
            relevanceScore = 0.98f,
            publishedTimeMillis = System.currentTimeMillis() - 3600000,
            recommendationReason = RecommendationReason("CORE_MATCH", "Direct matches with your high interest in Artificial Intelligence.")
        )
    )

    // --- State Storage ---

    private val defaultActiveInterests = mapOf(
        "topic_ai" to 0.9f,
        "topic_compose" to 0.75f,
        "topic_sleep" to 0.85f,
        "topic_mars" to 0.6f
    )

    private val defaultViewHistory = listOf("topic_ai", "topic_sleep", "topic_compose")

    private val _userInterestProfile = MutableStateFlow(
        UserInterestProfile(
            userId = "default_user_123",
            activeInterests = defaultActiveInterests,
            viewedTopicHistory = defaultViewHistory,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    )
    override val userInterestProfile: StateFlow<UserInterestProfile> = _userInterestProfile.asStateFlow()

    // --- Implementation --

    override suspend fun getInterestProfile(): UserInterestProfile {
        return _userInterestProfile.value
    }

    override suspend fun getAllCategories(): List<ContentCategory> {
        return categories
    }

    override suspend fun getAllTopics(): List<InterestTopic> {
        return topics
    }

    override suspend fun addInterest(topicId: String, initialWeight: Float) {
        _userInterestProfile.update { current ->
            val updatedMap = current.activeInterests.toMutableMap()
            updatedMap[topicId] = initialWeight.coerceIn(0.0f, 1.0f)
            current.copy(
                activeInterests = updatedMap,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        }
    }

    override suspend fun removeInterest(topicId: String) {
        _userInterestProfile.update { current ->
            val updatedMap = current.activeInterests.toMutableMap()
            updatedMap.remove(topicId)
            current.copy(
                activeInterests = updatedMap,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        }
    }

    override suspend fun updateInterestWeight(topicId: String, weight: Float) {
        _userInterestProfile.update { current ->
            if (current.activeInterests.containsKey(topicId)) {
                val updatedMap = current.activeInterests.toMutableMap()
                updatedMap[topicId] = weight.coerceIn(0.0f, 1.0f)
                current.copy(
                    activeInterests = updatedMap,
                    lastUpdatedMillis = System.currentTimeMillis()
                )
            } else {
                current
            }
        }
    }

    override suspend fun trackViewedTopic(topicId: String) {
        _userInterestProfile.update { current ->
            val updatedHistory = current.viewedTopicHistory.toMutableList()
            // Pull existing to front / add to front of history queue (max 20)
            updatedHistory.remove(topicId)
            updatedHistory.add(0, topicId)
            if (updatedHistory.size > 20) {
                updatedHistory.removeAt(updatedHistory.lastIndex)
            }
            
            // Slightly dynamically bump active interest weight if it exists (e.g., implicit interest feedback loop)
            val updatedMap = current.activeInterests.toMutableMap()
            val currentWeight = updatedMap[topicId]
            if (currentWeight != null) {
                updatedMap[topicId] = (currentWeight + 0.05f).coerceAtMost(1.0f)
            }

            current.copy(
                viewedTopicHistory = updatedHistory,
                activeInterests = updatedMap,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        }
    }

    override suspend fun getRecommendations(limit: Int): List<RelatedContent> {
        val activeInterests = _userInterestProfile.value.activeInterests
        
        return staticRecommendations.map { item ->
            // Calculate a personalized relevance score based on overlaps with active interests
            var preferenceMultiplier = 0.3f // Base multiplier for items with no interest overlap
            
            val intersection = item.topics.filter { activeInterests.containsKey(it.id) }
            if (intersection.isNotEmpty()) {
                val avgWeight = intersection.map { activeInterests[it.id] ?: 0f }.average().toFloat()
                preferenceMultiplier = 0.5f + (0.5f * avgWeight) // ranges from 0.5 to 1.0
            }

            val dynamicRelevance = item.relevanceScore * preferenceMultiplier
            item.copy(relevanceScore = dynamicRelevance)
        }
        .sortedByDescending { it.relevanceScore }
        .take(limit)
    }

    override suspend fun resetProfileToDefaults() {
        _userInterestProfile.value = UserInterestProfile(
            userId = "default_user_123",
            activeInterests = defaultActiveInterests,
            viewedTopicHistory = defaultViewHistory,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }
}
