package com.example

import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class AiIntelModelsTest {

    @Test
    fun testPlatformIntelGeneration_X() {
        val intel = MockData.generateMockPlatformIntel(Platform.X)
        
        assertEquals(Platform.X, intel.platform)
        assertEquals("Highly Energetic / Tech-Focus", intel.overallSentiment)
        assertTrue(intel.volumeTrend.contains("+42%"))
        assertEquals(IntelCategory.TECHNOLOGY, intel.topCategory)
        assertTrue(intel.trendingTopics.isNotEmpty())
        
        // Assert on the trending topics
        val modernCompose = intel.trendingTopics.first { it.title == "#ModernCompose" }
        assertEquals("inte_t1", modernCompose.id)
        assertEquals(2400000L, modernCompose.impressions)
        assertEquals(4.2, modernCompose.velocity, 0.01)
        assertEquals(IntelCategory.TECHNOLOGY, modernCompose.intelCategory)
        
        // Assert topic summary presence
        assertNotNull(modernCompose.topicSummary)
        val summary = modernCompose.topicSummary!!
        assertEquals("Compose compiler skip-by-default optimizations", summary.mainConcept)
        assertTrue(summary.summaryText.contains("Stability models"))
        assertEquals(3, summary.keyBulletPoints.size)
        
        // Assert why trending and detailed support features
        assertTrue(modernCompose.whyTrending.contains("35% lag reduction"))
        assertEquals(3, modernCompose.mentionedAccounts.size)
        assertEquals("@ellie_compose", modernCompose.mentionedAccounts[0].handle)
        assertEquals("Ellie Taylor", modernCompose.mentionedAccounts[0].displayName)
        assertEquals("125K followers", modernCompose.mentionedAccounts[0].followerCount)
        
        // Assert content examples
        assertEquals(2, modernCompose.contentExamples.size)
        assertTrue(modernCompose.contentExamples[0].contains("#JetpackCompose"))
    }

    @Test
    fun testPlatformIntelGeneration_Instagram() {
        val intel = MockData.generateMockPlatformIntel(Platform.INSTAGRAM)
        
        assertEquals(Platform.INSTAGRAM, intel.platform)
        assertEquals("Aesthetic / Motion Design Boom", intel.overallSentiment)
        assertEquals(IntelCategory.CREATIVE, intel.topCategory)
        assertTrue(intel.trendingTopics.size >= 2)
        
        val reelsAlgo = intel.trendingTopics.first { it.title == "reels_algorithm" }
        assertEquals(IntelCategory.CREATIVE, reelsAlgo.intelCategory)
        assertNotNull(reelsAlgo.topicSummary)
        assertEquals("Micro-education templates and dynamic transitions", reelsAlgo.topicSummary?.mainConcept)
        assertTrue(reelsAlgo.whyTrending.contains("updated distribution algorithms"))
        assertEquals(2, reelsAlgo.mentionedAccounts.size)
        assertEquals("@android_craft", reelsAlgo.mentionedAccounts[0].handle)
    }

    @Test
    fun testPlatformIntelGeneration_YouTube() {
        val intel = MockData.generateMockPlatformIntel(Platform.YOUTUBE)
        
        assertEquals(Platform.YOUTUBE, intel.platform)
        assertEquals("Educational / Long-Form Tutorials", intel.overallSentiment)
        assertEquals(IntelCategory.EDUCATION, intel.topCategory)
        
        val jetpackTips = intel.trendingTopics.first { it.title == "JetpackTips" }
        assertEquals(IntelCategory.EDUCATION, jetpackTips.intelCategory)
        assertNotNull(jetpackTips.topicSummary)
        assertTrue(jetpackTips.topicSummary!!.keyBulletPoints.size >= 2)
        assertEquals(3, jetpackTips.mentionedAccounts.size)
        assertTrue(jetpackTips.contentExamples.isNotEmpty())
    }
}
