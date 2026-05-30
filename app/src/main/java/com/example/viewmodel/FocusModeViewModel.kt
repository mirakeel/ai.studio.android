package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.FocusModeRepository
import com.example.repository.MockFocusModeRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FocusModeViewModel(
    private val repository: FocusModeRepository = MockFocusModeRepository
) : ViewModel() {

    val focusModeSettings: StateFlow<FocusModeSettings> = repository.focusModeSettings

    fun toggleFocusMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = repository.loadFilters()
            val updated = current.copy(isEnabled = enabled)
            repository.saveFilters(updated)
        }
    }

    fun updateFilterState(platform: Platform, filterType: FilterType, enabled: Boolean) {
        viewModelScope.launch {
            val current = repository.loadFilters()
            val platformFilters = current.platformFilters[platform] ?: PlatformFilters(platform, emptyMap())
            val updatedFiltersMap = platformFilters.enabledFilters.toMutableMap()
            updatedFiltersMap[filterType] = enabled
            
            val updatedPlatformFilters = platformFilters.copy(enabledFilters = updatedFiltersMap)
            val updatedPlatformMap = current.platformFilters.toMutableMap()
            updatedPlatformMap[platform] = updatedPlatformFilters
            
            val updatedSettings = current.copy(platformFilters = updatedPlatformMap)
            repository.saveFilters(updatedSettings)
        }
    }

    fun saveSettings(settings: FocusModeSettings) {
        viewModelScope.launch {
            repository.saveFilters(settings)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            repository.resetFilters()
        }
    }
}
