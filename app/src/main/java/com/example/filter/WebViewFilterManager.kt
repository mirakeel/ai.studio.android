package com.example.filter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import com.example.model.Platform
import java.util.Locale

class WebViewFilterManager private constructor() {

    private val handlers = mapOf(
        Platform.INSTAGRAM to InstagramFilterHandler(),
        Platform.X to XFilterHandler(),
        Platform.YOUTUBE to YouTubeFilterHandler()
    )

    fun getHandler(platform: Platform): PlatformFilterHandler {
        return handlers[platform] ?: throw IllegalArgumentException("No filter handler found for platform: $platform")
    }

    fun getHandlerForUrl(url: String): PlatformFilterHandler? {
        val lowerUrl = url.lowercase(Locale.ROOT)
        return when {
            lowerUrl.contains("instagram.com") -> getHandler(Platform.INSTAGRAM)
            lowerUrl.contains("twitter.com") || lowerUrl.contains("x.com") -> getHandler(Platform.X)
            lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be") -> getHandler(Platform.YOUTUBE)
            else -> null
        }
    }

    /**
     * Executes the combined JS pipeline script on the WebView.
     * Ensures all rules are evaluated on the UI Thread.
     */
    fun applyFilters(webView: WebView, platform: Platform, onResult: ((String?) -> Unit)? = null) {
        val handler = handlers[platform] ?: return
        val script = handler.compilePipelineScript()
        if (script.isBlank()) {
            onResult?.invoke("No active rules")
            return
        }

        // Must run WebView operations safely on the UI Main Thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            executeOnWebView(webView, script, onResult)
        } else {
            Handler(Looper.getMainLooper()).post {
                executeOnWebView(webView, script, onResult)
            }
        }
    }

    private fun executeOnWebView(webView: WebView, script: String, onResult: ((String?) -> Unit)?) {
        try {
            // Enable JavaScript if not already active
            if (!webView.settings.javaScriptEnabled) {
                webView.settings.javaScriptEnabled = true
                Log.d(TAG, "Temporarily enabled JavaScript to run filter rules.")
            }
            
            webView.evaluateJavascript(script) { value ->
                Log.d(TAG, "Pipeline executed: $value")
                onResult?.invoke(value)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed running JS evaluation pipeline", e)
            onResult?.invoke("ERROR: ${e.message}")
        }
    }

    /**
     * Diagnostic pipeline simulation dry-runs rules to log rule specifications.
     */
    fun dryRunPipeline(platform: Platform): List<PipelineResult> {
        val handler = handlers[platform] ?: return emptyList()
        val results = mutableListOf<PipelineResult>()
        
        for (rule in handler.getRules()) {
            val startTime = System.currentTimeMillis()
            val isActive = rule.isActive
            val success = !rule.selector.isBlank()
            
            results.add(
                PipelineResult(
                    ruleId = rule.id,
                    success = success,
                    affectedCount = if (isActive && success) 1 else 0,
                    executionTimeMs = System.currentTimeMillis() - startTime,
                    errorMessage = if (rule.selector.isBlank()) "Empty selector is invalid" else null
                )
            )
        }
        return results
    }

    companion object {
        private const val TAG = "WebViewFilterManager"
        
        @Volatile
        private var instance: WebViewFilterManager? = null

        fun getInstance(): WebViewFilterManager {
            return instance ?: synchronized(this) {
                instance ?: WebViewFilterManager().also { instance = it }
            }
        }
    }
}
