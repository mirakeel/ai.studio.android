package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.UserSettings
import com.example.repository.SocialDashRepository
import kotlinx.coroutines.flow.asStateFlow

class SocialDashViewModel(private val repository: SocialDashRepository = SocialDashRepository()) : ViewModel() {
    val settings = repository.settings
    
    fun getUsageStats() = repository.getUsageStats()
    fun getTrendingTopics() = repository.getTrendingTopics()
    fun getPlatformDigests() = repository.getPlatformDigests()

    fun updateSettings(newSettings: UserSettings) {
        repository.updateSettings(newSettings)
    }
}
