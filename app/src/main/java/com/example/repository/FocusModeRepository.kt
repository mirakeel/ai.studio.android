package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface FocusModeRepository {
    val focusModeSettings: StateFlow<FocusModeSettings>
    suspend fun loadFilters(): FocusModeSettings
    suspend fun saveFilters(settings: FocusModeSettings)
    suspend fun resetFilters()
}

object MockFocusModeRepository : FocusModeRepository {
    private val _focusModeSettings = MutableStateFlow(createInitialMockSettings())
    override val focusModeSettings: StateFlow<FocusModeSettings> = _focusModeSettings.asStateFlow()

    override suspend fun loadFilters(): FocusModeSettings {
        return _focusModeSettings.value
    }

    override suspend fun saveFilters(settings: FocusModeSettings) {
        _focusModeSettings.value = settings
    }

    override suspend fun resetFilters() {
        _focusModeSettings.value = FocusModeSettings.createDefault()
    }

    private fun createInitialMockSettings(): FocusModeSettings {
        val filters = Platform.values().associateWith { platform ->
            val platformFilters = FilterType.values().filter { it.platform == platform }
                .associateWith { filterType ->
                    when (filterType) {
                        FilterType.HIDE_REELS -> true
                        FilterType.HIDE_EXPLORE -> true
                        FilterType.HIDE_SUGGESTED_POSTS -> true
                        FilterType.HIDE_SUGGESTED_ACCOUNTS -> true
                        FilterType.HIDE_FOR_YOU -> true
                        FilterType.HIDE_SHORTS -> true
                        else -> false
                    }
                }
            PlatformFilters(platform, platformFilters)
        }
        return FocusModeSettings(isEnabled = true, platformFilters = filters)
    }
}
