package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analyzer.AnalysisResult
import com.example.analyzer.ContentAnalyzer
import com.example.analyzer.MockContentAnalyzer
import com.example.analyzer.PostContentInput
import com.example.api.NetworkProvider
import com.example.model.*
import com.example.repository.SocialDashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RequestState<out T> {
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()
}

class SocialDashViewModel(
    private val repository: SocialDashRepository = NetworkProvider.repository,
    private val analyzer: ContentAnalyzer = MockContentAnalyzer()
) : ViewModel() {

    private val _usageStats = MutableStateFlow<List<UsageStats>>(emptyList())
    val usageStats: StateFlow<List<UsageStats>> = _usageStats.asStateFlow()

    private val _trackerStats = MutableStateFlow<List<UsageStats>>(emptyList())
    val trackerStats: StateFlow<List<UsageStats>> = _trackerStats.asStateFlow()

    private val _trendingTopics = MutableStateFlow<List<TrendingTopic>>(emptyList())
    val trendingTopics: StateFlow<List<TrendingTopic>> = _trendingTopics.asStateFlow()

    private val _platformInfo = MutableStateFlow<List<PlatformInfo>>(emptyList())
    val platformInfo: StateFlow<List<PlatformInfo>> = _platformInfo.asStateFlow()

    private val _aiAnalysisState = MutableStateFlow<RequestState<PlatformAnalysis>>(RequestState.Idle)
    val aiAnalysisState: StateFlow<RequestState<PlatformAnalysis>> = _aiAnalysisState.asStateFlow()

    private val _analysisResult = MutableStateFlow<RequestState<AnalysisResult>>(RequestState.Idle)
    val analysisResult: StateFlow<RequestState<AnalysisResult>> = _analysisResult.asStateFlow()

    private val _latestDigest = MutableStateFlow<Digest?>(null)
    val latestDigest: StateFlow<Digest?> = _latestDigest.asStateFlow()

    private val _dailyBriefState = MutableStateFlow<RequestState<AIDailyBrief>>(RequestState.Idle)
    val dailyBriefState: StateFlow<RequestState<AIDailyBrief>> = _dailyBriefState.asStateFlow()

    val settings = repository.settings

    init {
        // Data is refreshed via LaunchedEffect in screens to avoid blocking init
    }

    fun refreshData() {
        viewModelScope.launch {
            _usageStats.value = repository.getUsageStats()
            _trackerStats.value = repository.getTrackerStats()
            _trendingTopics.value = repository.getTrendingTopics()
            _platformInfo.value = repository.getPlatformInfo()
            repository.fetchSettings()
        }
    }

    fun sendHeartbeat(platformId: String) {
        viewModelScope.launch {
            repository.sendHeartbeat(platformId, 60)
            // Optionally refresh tracker stats after heartbeat if needed
            _trackerStats.value = repository.getTrackerStats()
        }
    }

    fun fetchLatestDigest(platformId: String) {
        viewModelScope.launch {
            _latestDigest.value = repository.getLatestDigest(platformId)
        }
    }

    fun generateRealTimeAnalysis(platformId: String) {
        viewModelScope.launch {
            _aiAnalysisState.value = RequestState.Loading
            val result = repository.generateAnalysis(platformId)
            if (result != null) {
                _aiAnalysisState.value = RequestState.Success(result)
            } else {
                _aiAnalysisState.value = RequestState.Error("Failed to generate analysis. Please check your connection.")
            }
        }
    }

    fun analyzeContent(input: PostContentInput) {
        viewModelScope.launch {
            _analysisResult.value = RequestState.Loading
            try {
                val result = analyzer.analyze(input)
                _analysisResult.value = RequestState.Success(result)
            } catch (e: Exception) {
                _analysisResult.value = RequestState.Error(e.message ?: "Analysis failed")
            }
        }
    }

    fun resetAiAnalysis() {
        _aiAnalysisState.value = RequestState.Idle
        _analysisResult.value = RequestState.Idle
        _dailyBriefState.value = RequestState.Idle
    }

    fun generateDailyBrief() {
        viewModelScope.launch {
            _dailyBriefState.value = RequestState.Loading
            try {
                // Simulating processing delay for professional feel
                kotlinx.coroutines.delay(1200)
                
                val brief = AIDailyBrief(
                    instagramBrief = PlatformSummary(
                        summaryText = "Instagram engagement spiked by +28%, driven heavily by visual-first content and tutorial reels. Your tech-setup reels are gathering major traction.",
                        keyEvents = listOf(
                            "New algorithm update favoring repeatable mini-code exercises.",
                            "Your layout dynamic spring animation Reel reached 950K impressions.",
                            "Significant increase in comments seeking source code access."
                        ),
                        topDiscussions = listOf(
                            "Aesthetic dark editor themes and customized visual setups.",
                            "Performance and screen-refresh stability in motion design."
                        ),
                        recommendedTopics = listOf(
                            "GlassmorphicWidgets",
                            "ComposeMotionDesign",
                            "TechStudioSetups"
                        )
                    ),
                    xBrief = PlatformSummary(
                        summaryText = "#ModernCompose is dominating tech conversations. Multi-platform stability updates have sparked widespread excitement and debate among enterprise framework designers.",
                        keyEvents = listOf(
                            "Google Advocate team published benchmark results with 35% performance improvement.",
                            "Major multi-module codebases announced migration plans to Kotlin K2.",
                            "Strong debate over elimination of explicit stability annotations."
                        ),
                        topDiscussions = listOf(
                            "Automatic skipped-by-default recompositions in Jetpack Compose.",
                            "K2 Compiler compile-time gains for large projects."
                        ),
                        recommendedTopics = listOf(
                            "ModernCompose",
                            "K2CompilerRoadmap",
                            "KotlinMultiplatform"
                        )
                    ),
                    youtubeBrief = PlatformSummary(
                        summaryText = "Long-form technical walkthroughs and deep-dives on compiler optimizations are showing massive click-through rates. Audience is leaning heavily into concrete migration guides.",
                        keyEvents = listOf(
                            "Type-safe navigation tutorials are dominating Android developers' feeds.",
                            "Compilation-speed comparison between KAPT and KSP for Room integration gains traction.",
                            "Best practices blueprints for offline-first architecture published by official Android channels."
                        ),
                        topDiscussions = listOf(
                            "How to fully migrate large graphs to type-safe Compose routing.",
                            "Transitioning legacy projects to Kotlin Symbol Processing (KSP) safely."
                        ),
                        recommendedTopics = listOf(
                            "JetpackTips",
                            "RoomDatabasesBestPractices",
                            "KSPMigrationGuide"
                        )
                    )
                )
                _dailyBriefState.value = RequestState.Success(brief)
            } catch (e: Exception) {
                _dailyBriefState.value = RequestState.Error(e.message ?: "Failed to assemble AI Daily Brief")
            }
        }
    }

    fun resetDailyBrief() {
        _dailyBriefState.value = RequestState.Idle
    }

    fun getUsageStats() = _usageStats.value
    fun getTrendingTopics() = _trendingTopics.value
    fun getPlatformDigests() = _platformInfo.value

    fun updateSettings(newSettings: UserSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }
}
