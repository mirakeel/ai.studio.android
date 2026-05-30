package com.example.focus

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import com.example.model.FocusModeSettings
import com.example.model.Platform

/**
 * FocusModeExecutor triggers the executed focus pipeline on target webviews.
 * Ensures tasks run safely on the Android UI Thread.
 */
class FocusModeExecutor private constructor() {

    private val injectionManager = JavascriptInjectionManager.getInstance()

    /**
     * Executes the compiled platform focus script.
     * Passes back success callback.
     */
    fun executeFocusMode(
        webView: WebView,
        platform: Platform,
        settings: FocusModeSettings,
        onCompleted: (Boolean) -> Unit = {}
    ) {
        val script = injectionManager.compileFocusScript(platform, settings)
        if (script.isBlank()) {
            onCompleted(false)
            Log.d(TAG, "Focus script is empty or disabled for ${platform.name}")
            return
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            runOnWebView(webView, script, onCompleted)
        } else {
            Handler(Looper.getMainLooper()).post {
                runOnWebView(webView, script, onCompleted)
            }
        }
    }

    private fun runOnWebView(webView: WebView, script: String, onCompleted: (Boolean) -> Unit) {
        try {
            // Guarantee javascript execution is enabled
            if (!webView.settings.javaScriptEnabled) {
                webView.settings.javaScriptEnabled = true
                Log.d(TAG, "Temporarily enabled javaScript on target webview for injection.")
            }

            webView.evaluateJavascript(script) { result ->
                Log.d(TAG, "Evaluated successfully in WebView: $result")
                onCompleted(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing Focus Mode scripting: ", e)
            onCompleted(false)
        }
    }

    companion object {
        private const val TAG = "FocusModeExecutor"

        @Volatile
        private var instance: FocusModeExecutor? = null

        fun getInstance(): FocusModeExecutor {
            return instance ?: synchronized(this) {
                instance ?: FocusModeExecutor().also { instance = it }
            }
        }
    }
}
