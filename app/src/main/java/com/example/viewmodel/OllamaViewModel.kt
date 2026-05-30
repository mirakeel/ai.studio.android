package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.NetworkProvider
import com.example.repository.OllamaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OllamaUiState<out T> {
    object Idle : OllamaUiState<Nothing>()
    object Loading : OllamaUiState<Nothing>()
    data class Success<out T>(val data: T) : OllamaUiState<T>()
    data class Error(val message: String) : OllamaUiState<Nothing>()
}

class OllamaViewModel(
    private val repository: OllamaRepository = OllamaRepository(NetworkProvider.ollamaService)
) : ViewModel() {

    private val _uiState = MutableStateFlow<OllamaUiState<String>>(OllamaUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun generatePlatformDigest(platform: String) {
        viewModelScope.launch {
            _uiState.value = OllamaUiState.Loading
            try {
                val result = repository.generatePlatformDigest(platform)
                _uiState.value = OllamaUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = OllamaUiState.Error(e.message ?: "Failed to generate digest")
            }
        }
    }

    fun generateTopicSummary(topic: String) {
        viewModelScope.launch {
            _uiState.value = OllamaUiState.Loading
            try {
                val result = repository.generateTopicSummary(topic)
                _uiState.value = OllamaUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = OllamaUiState.Error(e.message ?: "Failed to generate summary")
            }
        }
    }

    fun generateTrendingExplanation(trendingTopic: String) {
        viewModelScope.launch {
            _uiState.value = OllamaUiState.Loading
            try {
                val result = repository.generateTrendingExplanation(trendingTopic)
                _uiState.value = OllamaUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = OllamaUiState.Error(e.message ?: "Failed to generate explanation")
            }
        }
    }
}
