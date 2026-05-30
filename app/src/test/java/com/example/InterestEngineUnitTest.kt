package com.example

import com.example.repository.MockInterestEngineRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class InterestEngineUnitTest {

    private val repository = MockInterestEngineRepository

    @Before
    fun setUp() = runTest {
        repository.resetProfileToDefaults()
    }

    @Test
    fun testDefaultProfile() = runTest {
        val profile = repository.getInterestProfile()
        assertNotNull(profile)
        assertEquals("default_user_123", profile.userId)
        assertTrue(profile.activeInterests.containsKey("topic_ai"))
        assertEquals(0.9f, profile.activeInterests["topic_ai"] ?: 0f, 0.001f)
    }

    @Test
    fun testAddAndRemoveInterest() = runTest {
        // Add a new interest
        repository.addInterest("topic_kotlin", 0.7f)
        var profile = repository.getInterestProfile()
        assertTrue(profile.activeInterests.containsKey("topic_kotlin"))
        assertEquals(0.7f, profile.activeInterests["topic_kotlin"] ?: 0f, 0.001f)

        // Remove the interest
        repository.removeInterest("topic_kotlin")
        profile = repository.getInterestProfile()
        assertFalse(profile.activeInterests.containsKey("topic_kotlin"))
    }

    @Test
    fun testUpdateInterestWeight() = runTest {
        // Update existing interest weight
        repository.updateInterestWeight("topic_ai", 0.95f)
        var profile = repository.getInterestProfile()
        assertEquals(0.95f, profile.activeInterests["topic_ai"] ?: 0f, 0.001f)

        // Attempting to update a non-existent interest should do nothing or be ignored
        repository.updateInterestWeight("non_existent", 0.4f)
        profile = repository.getInterestProfile()
        assertFalse(profile.activeInterests.containsKey("non_existent"))
    }

    @Test
    fun testTrackViewedTopic() = runTest {
        // Track a brand-new viewed topic
        repository.trackViewedTopic("topic_unreal")
        val profile = repository.getInterestProfile()
        
        // Assert it is at the front of the viewed history
        assertEquals("topic_unreal", profile.viewedTopicHistory.first())
        
        // Tracking an existing active interest (e.g. topic_ai) should slightly boost its weight
        val oldWeight = repository.getInterestProfile().activeInterests["topic_ai"] ?: 0f
        repository.trackViewedTopic("topic_ai")
        val newWeight = repository.getInterestProfile().activeInterests["topic_ai"] ?: 0f
        
        assertTrue(newWeight > oldWeight || oldWeight >= 1.0f)
    }

    @Test
    fun testGetRecommendations() = runTest {
        val recommendations = repository.getRecommendations(limit = 3)
        assertEquals(3, recommendations.size)
        
        // Assert they are sorted in descending order of relevanceScore
        assertTrue(recommendations[0].relevanceScore >= recommendations[1].relevanceScore)
        assertTrue(recommendations[1].relevanceScore >= recommendations[2].relevanceScore)
    }
}
