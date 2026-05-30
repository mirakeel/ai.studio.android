package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SocialDashRepository {
    private val _settings = MutableStateFlow(MockData.getMockSettings())
    val settings = _settings.asStateFlow()

    fun getUsageStats() = MockData.getMockUsageStats()
    fun getTrendingTopics() = MockData.getMockTrendingTopics()
    fun getPlatformDigests() = MockData.platforms // Placeholder for digests

    fun updateSettings(newSettings: UserSettings) {
        _settings.update { newSettings }
    }
}
