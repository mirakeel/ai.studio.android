package com.example.filter

import com.example.model.Platform

enum class FilterRuleType {
    HIDE_ELEMENT,
    HIGHLIGHT_ELEMENT,
    SIMPLIFY_TYPOGRAPHY,
    CUSTOM_JS
}

data class FilterRule(
    val id: String,
    val name: String,
    val description: String,
    val platform: Platform,
    val type: FilterRuleType,
    val selector: String,
    val isActive: Boolean = true,
    val customValue: String? = null
)

data class PipelineResult(
    val ruleId: String,
    val success: Boolean,
    val affectedCount: Int = 0,
    val executionTimeMs: Long = 0,
    val errorMessage: String? = null
)
