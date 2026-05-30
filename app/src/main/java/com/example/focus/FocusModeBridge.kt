package com.example.focus

import android.util.Log
import android.webkit.JavascriptInterface

/**
 * FocusModeBridge acts as a secure boundary for injected JavaScript modules
 * to log operations and send telemetry back to the native Android host.
 */
class FocusModeBridge {

    @JavascriptInterface
    fun logHiddenCount(platform: String, count: Int, details: String) {
        Log.d(TAG, "[$platform] POC - Proactively hidden $count distractions: $details")
    }

    @JavascriptInterface
    fun logInfo(message: String) {
        Log.d(TAG, "JS Info: $message")
    }

    @JavascriptInterface
    fun logError(message: String) {
        Log.e(TAG, "JS Error: $message")
    }

    companion object {
        private const val TAG = "FocusModeBridge"
    }
}
