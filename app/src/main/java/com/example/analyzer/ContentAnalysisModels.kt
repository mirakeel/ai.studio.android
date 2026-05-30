package com.example.analyzer

data class PostContentInput(
    val title: String,
    val caption: String,
    val description: String,
    val metadata: Map<String, String>
)

data class AnalysisResult(
    val topicTags: List<String>,
    val category: String,
    val keywords: List<String>,
    val recommendationWeight: Double
)
