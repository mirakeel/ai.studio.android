package com.example.model

import com.example.api.dto.*

fun PlatformInfoDTO.toDomain() = PlatformInfo(
    platform = try { Platform.valueOf(platform.uppercase()) } catch (e: Exception) { Platform.X },
    displayName = displayName,
    isConnected = isConnected
)

fun DigestDTO.toDomain() = Digest(
    id = id,
    platform = try { Platform.valueOf(platform.uppercase()) } catch (e: Exception) { Platform.X },
    content = content,
    timestamp = timestamp
)

fun TrendingTopicDTO.toDomain() = TrendingTopic(
    id = id,
    platform = try { Platform.valueOf(platform.uppercase()) } catch (e: Exception) { Platform.X },
    title = title,
    impressions = impressions,
    velocity = velocity
)

fun UsageStatsDTO.toDomain() = UsageStats(
    platform = try { Platform.valueOf(platform.uppercase()) } catch (e: Exception) { Platform.X },
    dailyImpressions = dailyImpressions,
    engagementRate = engagementRate,
    minutesToday = minutesToday,
    minutesWeek = minutesWeek,
    minutesMonth = minutesMonth
)

fun UserSettingsDTO.toDomain() = UserSettings(
    aiDigestsEnabled = aiDigestsEnabled.mapKeys { 
        try { Platform.valueOf(it.key.uppercase()) } catch (e: Exception) { Platform.X }
    },
    notificationStyle = try { NotificationStyle.valueOf(notificationStyle.uppercase()) } catch (e: Exception) { NotificationStyle.IN_APP },
    syncFrequencyMinutes = syncFrequencyMinutes,
    morningBriefingTime = morningBriefingTime,
    interestTopics = interestTopics.toSet(),
    activityFilters = activityFilters.mapNotNull { 
        try { ActivityFilter.valueOf(it.uppercase()) } catch (e: Exception) { null }
    }.toSet()
)

fun UserSettings.toDTO() = UserSettingsDTO(
    aiDigestsEnabled = aiDigestsEnabled.mapKeys { it.key.name },
    notificationStyle = notificationStyle.name,
    syncFrequencyMinutes = syncFrequencyMinutes,
    morningBriefingTime = morningBriefingTime,
    interestTopics = interestTopics.toList(),
    activityFilters = activityFilters.map { it.name }
)

fun AnalysisResponseDTO.toDomain() = PlatformAnalysis(
    platform = try { Platform.valueOf(platform.uppercase()) } catch (e: Exception) { Platform.X },
    analysisText = analysisText,
    timestamp = timestamp
)
