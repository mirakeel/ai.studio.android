package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filter.FilterRule
import com.example.filter.PipelineResult
import com.example.filter.WebViewFilterManager
import com.example.model.Platform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusFilterViewModel(
    private val filterManager: WebViewFilterManager = WebViewFilterManager.getInstance()
) : ViewModel() {

    private val _platformRules = MutableStateFlow<Map<Platform, List<FilterRule>>>(emptyMap())
    val platformRules: StateFlow<Map<Platform, List<FilterRule>>> = _platformRules.asStateFlow()

    private val _diagnosticResults = MutableStateFlow<List<PipelineResult>>(emptyList())
    val diagnosticResults: StateFlow<List<PipelineResult>> = _diagnosticResults.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        refreshAllRules()
    }

    /**
     * Re-fetch the latest in-memory rule sets for all supported platforms.
     */
    fun refreshAllRules() {
        val rulesMap = mutableMapOf<Platform, List<FilterRule>>()
        Platform.values().forEach { platform ->
            try {
                val handler = filterManager.getHandler(platform)
                rulesMap[platform] = handler.getRules()
            } catch (e: Exception) {
                // If a platform is not registered, ignore or handle gracefully
            }
        }
        _platformRules.value = rulesMap
    }

    /**
     * Interactive toggle switch to dynamically activate/deactivate a filter rule.
     */
    fun toggleRule(platform: Platform, ruleId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                filterManager.getHandler(platform).toggleRule(ruleId, isEnabled)
                refreshAllRules()
            } catch (e: Exception) {
                // Log and absorb
            }
        }
    }

    /**
     * Executes diagnostic pipeline checks to test selector performance and log analytics.
     */
    fun runPerformanceDiagnostics(platform: Platform) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val results = filterManager.dryRunPipeline(platform)
                _diagnosticResults.value = results
            } finally {
                _isProcessing.value = false
            }
        }
    }

    /**
     * Instantly compiles and applies the JavaScript rules down to target WebView instances.
     */
    fun executeInWebView(webView: android.webkit.WebView, platform: Platform, onFinished: (String?) -> Unit) {
        viewModelScope.launch {
            _isProcessing.value = true
            filterManager.applyFilters(webView, platform) { result ->
                _isProcessing.value = false
                onFinished(result)
            }
        }
    }
}
