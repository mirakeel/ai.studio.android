package com.example.analyzer

interface ContentAnalyzer {
    suspend fun analyze(input: PostContentInput): AnalysisResult
}
