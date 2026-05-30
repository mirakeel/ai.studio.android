package com.example.session

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.model.Platform

class PlatformWebViewManager private constructor(private val context: Context) {

    private val sessionManager = SessionManager.getInstance(context)

    /**
     * Create and securely configure a WebView instance for a given platform.
     */
    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    fun createPlatformWebView(platform: Platform): WebView {
        val webView = WebView(context)
        val settings = webView.settings

        // 1. Core performance features required for modern heavy SPA web views
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // 2. Security hardening (Disabling file and content access to prevent cross-site file leaks)
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.allowFileAccessFromFileURLs = false
        settings.allowUniversalAccessFromFileURLs = false

        // 3. Cookie and Session configuration
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        // Register the JS injection Bridge interface for Focus Mode logging/telemetry
        webView.addJavascriptInterface(com.example.focus.FocusModeBridge(), "FocusModeBridge")

        // 4. Configure robust WebViewClient to monitor logins and sessions
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                
                // Flush cookies to disk so current sessions survive restart/crash/reboot
                sessionManager.flushCookies()

                // Check cookies to see if user has logged in
                url?.let { currentUrl ->
                    val cookies = cookieManager.getCookie(currentUrl)
                    if (!cookies.isNullOrBlank()) {
                        val isUserLoggedIn = when (platform) {
                            Platform.INSTAGRAM -> cookies.contains("sessionid") || cookies.contains("ds_user_id")
                            Platform.X -> cookies.contains("auth_token") || cookies.contains("twid")
                            Platform.YOUTUBE -> cookies.contains("LOGIN_INFO") || cookies.contains("SID")
                        }
                        if (isUserLoggedIn) {
                            sessionManager.setSessionActive(platform, true)
                        }
                    }
                }

                // Trigger Focus Mode execution automatically if enabled!
                val focusSettings = com.example.repository.MockFocusModeRepository.focusModeSettings.value
                if (focusSettings.isEnabled) {
                    com.example.focus.FocusModeExecutor.getInstance().executeFocusMode(
                        webView = webView,
                        platform = platform,
                        settings = focusSettings
                    ) { success ->
                        android.util.Log.d("PlatformWebViewManager", "Executed Focus Mode POC for ${platform.name}: success=$success")
                    }
                }
            }
        }

        return webView
    }

    companion object {
        @Volatile
        private var instance: PlatformWebViewManager? = null

        fun getInstance(context: Context): PlatformWebViewManager {
            return instance ?: synchronized(this) {
                instance ?: PlatformWebViewManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
