package com.example.analyzer

class MockContentAnalyzer : ContentAnalyzer {
    override suspend fun analyze(input: PostContentInput): AnalysisResult {
        // Simulating local processing with some delay
        kotlinx.coroutines.delay(500)
        
        return AnalysisResult(
            topicTags = listOf("technology", "mobile-dev", "kotlin"),
            category = "Education",
            keywords = listOf("development", "mobile", "android"),
            recommendationWeight = 0.85
        )
    }
}
