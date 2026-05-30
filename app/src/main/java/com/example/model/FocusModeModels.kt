package com.example.model

enum class FilterType(val id: String, val displayName: String, val platform: Platform) {
    // Instagram
    HIDE_REELS("hide_reels", "Hide Reels", Platform.INSTAGRAM),
    HIDE_EXPLORE("hide_explore", "Hide Explore", Platform.INSTAGRAM),
    HIDE_SUGGESTED_POSTS("hide_suggested_posts", "Hide Suggested Posts", Platform.INSTAGRAM),
    HIDE_SUGGESTED_ACCOUNTS("hide_suggested_accounts", "Hide Suggested Accounts", Platform.INSTAGRAM),

    // X
    HIDE_FOR_YOU("hide_for_you", "Hide For You", Platform.X),
    HIDE_TRENDING("hide_trending", "Hide Trending", Platform.X),
    HIDE_SUGGESTED_USERS("hide_suggested_users", "Hide Suggested Users", Platform.X),

    // YouTube
    HIDE_SHORTS("hide_shorts", "Hide Shorts", Platform.YOUTUBE),
    HIDE_HOME_RECOMMENDATIONS("hide_home_recs", "Hide Home Recommendations", Platform.YOUTUBE),
    HIDE_SUGGESTED_VIDEOS("hide_suggested_videos", "Hide Suggested Videos", Platform.YOUTUBE),
    HIDE_END_SCREEN_RECOMMENDATIONS("hide_end_screen_recs", "Hide End Screen Recommendations", Platform.YOUTUBE)
}

data class PlatformFilters(
    val platform: Platform,
    val enabledFilters: Map<FilterType, Boolean>
)

data class FocusModeSettings(
    val isEnabled: Boolean,
    val platformFilters: Map<Platform, PlatformFilters>
) {
    companion object {
        fun createDefault(): FocusModeSettings {
            val filters = Platform.values().associateWith { platform ->
                val platformFilters = FilterType.values().filter { it.platform == platform }
                    .associateWith { false }
                PlatformFilters(platform, platformFilters)
            }
            return FocusModeSettings(isEnabled = false, platformFilters = filters)
        }
    }
}
