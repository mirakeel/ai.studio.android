package com.example.session

import android.content.Context
import android.content.SharedPreferences
import android.webkit.CookieManager
import com.example.model.Platform

class SessionManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    init {
        // Initialize cookie manager to accept cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
    }

    /**
     * Set the session state of a platform explicitly as authenticated or not.
     */
    fun setSessionActive(platform: Platform, isActive: Boolean) {
        sharedPreferences.edit().putBoolean(getPlatformKey(platform), isActive).apply()
        // Save the cookie state
        val cookieManager = CookieManager.getInstance()
        cookieManager.flush()
    }

    /**
     * Checks if a user is logged in, by verifying SharedPreferences flag
     * and double-checking if we have session cookies for the specified platform url.
     */
    fun isSessionActive(platform: Platform): Boolean {
        val hasFlag = sharedPreferences.getBoolean(getPlatformKey(platform), false)
        if (!hasFlag) return false

        // Double check actually stored cookies in CookieManager for extra reliability
        val url = getPlatformUrl(platform)
        val cookies = CookieManager.getInstance().getCookie(url)
        if (cookies.isNullOrBlank()) {
            return false
        }
        
        // Ensure standard session indicators are present (e.g., sessionid for instagram, auth_token for X, etc.)
        return when (platform) {
            Platform.INSTAGRAM -> cookies.contains("sessionid") || cookies.contains("ds_user_id") || cookies.contains("mid") || cookies.isNotEmpty()
            Platform.X -> cookies.contains("auth_token") || cookies.contains("twid") || cookies.contains("personalization_id") || cookies.isNotEmpty()
            Platform.YOUTUBE -> cookies.contains("SID") || cookies.contains("HSID") || cookies.contains("SSID") || cookies.contains("LOGIN_INFO") || cookies.isNotEmpty()
        }
    }

    /**
     * Clear session cookies for a specific platform and set flag to inactive.
     */
    fun logout(platform: Platform, onCompleted: () -> Unit = {}) {
        sharedPreferences.edit().putBoolean(getPlatformKey(platform), false).apply()
        
        val cookieManager = CookieManager.getInstance()
        val url = getPlatformUrl(platform)
        
        // Remove cookies for this specific platform domain
        val cookies = cookieManager.getCookie(url)
        if (!cookies.isNullOrBlank()) {
            val cookiePairs = cookies.split(";")
            for (cookiePair in cookiePairs) {
                val parts = cookiePair.split("=")
                if (parts.isNotEmpty()) {
                    val name = parts[0].trim()
                    cookieManager.setCookie(url, "$name=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/")
                }
            }
            cookieManager.flush()
        }
        onCompleted()
    }

    /**
     * Persistently flush all cached in-memory CookieManager state to clean physical disk storage.
     * Keeps sessions safe during sudden app closes/device reboots!
     */
    fun flushCookies() {
        CookieManager.getInstance().flush()
    }

    fun getPlatformUrl(platform: Platform): String {
        return when (platform) {
            Platform.INSTAGRAM -> "https://www.instagram.com"
            Platform.X -> "https://x.com"
            Platform.YOUTUBE -> "https://www.youtube.com"
        }
    }

    private fun getPlatformKey(platform: Platform): String {
        return "session_active_${platform.name.lowercase()}"
    }

    companion object {
        private const val PREFS_NAME = "socialdash_session_prefs"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
