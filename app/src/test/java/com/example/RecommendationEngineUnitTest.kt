package com.example

import com.example.model.ContentItem
import com.example.model.Platform
import com.example.repository.RecommendationEngine
import org.junit.Assert.*
import org.junit.Test

class RecommendationEngineUnitTest {

    @Test
    fun testCalculateTopicSimilarity() {
        val fastapi = ContentItem("1", "FastAPI Tutorial", "Desc", Platform.YOUTUBE, listOf("Python", "Backend", "FastAPI"))
        val pythonApi = ContentItem("2", "Python API Guide", "Desc", Platform.YOUTUBE, listOf("Python", "API", "Backend"))
        
        // FastAPI and Python API Guide intersect on "Python" and "Backend".
        // Union is {"Python", "Backend", "FastAPI", "API"} -> size = 4
        // Intersection is {"Python", "Backend"} -> size = 2
        // Jaccard similarity = 2 / 4 = 0.5f
        
        val similarity = RecommendationEngine.calculateTopicSimilarity(fastapi, pythonApi)
        assertEquals(0.5f, similarity, 0.001f)
    }

    @Test
    fun testCalculateInterestScore() {
        val fastapi = ContentItem("1", "FastAPI Tutorial", "Desc", Platform.YOUTUBE, listOf("Python", "Backend", "FastAPI"))
        
        val userInterests = mapOf(
            "Python" to 0.8f,
            "Backend" to 1.0f
            // FastAPI has no entry
        )
        
        // Matches: Python (0.8), Backend (1.0). Average weight = 0.9f.
        // Match count = 2. Total topics of fastapi = 3.
        // Coverage ratio = 2 / 3 = 0.6667f
        // Expected Interest Score = 0.9f * (2f/3f) = 0.6f
        
        val interestScore = RecommendationEngine.calculateInterestScore(fastapi, userInterests)
        assertEquals(0.6f, interestScore, 0.001f)
    }

    @Test
    fun testGenerateRecommendationsWithSpecificUserExample() {
        // From user request:
        // Viewed: "FastAPI Tutorial" -> Python, Backend, FastAPI
        // Recommended should prioritize: "Python API Guide", "Backend Architecture Post", "Async Programming Tutorial"
        
        val viewedItem = RecommendationEngine.sampleViewedItems.first { it.title == "FastAPI Tutorial" }
        
        val userInterests = mapOf(
            "Python" to 0.9f,
            "Backend" to 0.9f,
            "FastAPI" to 0.8f
        )
        
        val recommendations = RecommendationEngine.recommend(
            viewedItem = viewedItem,
            userInterests = userInterests,
            limit = 3
        )
        
        assertEquals(3, recommendations.size)
        
        // Get the titles of the top 3 recommended items
        val recommendedTitles = recommendations.map { it.item.title }
        
        // Assert that the recommended items contain our exact target list from the example:
        assertTrue(recommendedTitles.contains("Python API Guide"))
        assertTrue(recommendedTitles.contains("Backend Architecture Post"))
        assertTrue(recommendedTitles.contains("Async Programming Tutorial"))
        
        // Additionally verify that they have detailed recommendation reasons
        recommendations.forEach { recommendation ->
            assertNotNull(recommendation.recommendationReason.description)
            assertNotNull(recommendation.recommendationReason.code)
            assertTrue(recommendation.matchingTopics.isNotEmpty())
        }
    }
}
