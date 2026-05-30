package com.example.repository

import com.example.api.OllamaService
import com.example.model.OllamaRequest

class OllamaRepository(private val ollamaService: OllamaService) {
    private val modelName = "llama3"

    suspend fun generatePlatformDigest(platform: String): String {
        val prompt = "Generate a digest for the platform: $platform."
        return ollamaService.generate(OllamaRequest(modelName, prompt)).response
    }

    suspend fun generateTopicSummary(topic: String): String {
        val prompt = "Summarize the topic: $topic."
        return ollamaService.generate(OllamaRequest(modelName, prompt)).response
    }

    suspend fun generateTrendingExplanation(trendingTopic: String): String {
        val prompt = "Explain why this is trending: $trendingTopic."
        return ollamaService.generate(OllamaRequest(modelName, prompt)).response
    }
}
