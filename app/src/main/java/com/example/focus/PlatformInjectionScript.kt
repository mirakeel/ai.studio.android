package com.example.focus

import com.example.model.FilterType
import com.example.model.Platform

/**
 * PlatformInjectionScript holds the metadata and mapping configuration of a supported
 * platform to compile dynamic injection scripts for Focus Mode.
 */
data class PlatformInjectionScript(
    val platform: Platform,
    val name: String,
    /**
     * Map of FilterType to lists of CSS selectors. If the filter is enabled
     * in settings, these selectors will be compiled to display:none!
     */
    val filterSelectors: Map<FilterType, List<String>>,
    /**
     * Set of standard or global selectors that should always be hidden on this platform.
     */
    val globalHideSelectors: List<String> = emptyList(),
    /**
     * Custom Platform JS script to execute if additional behavior alteration is required.
     */
    val customJs: String = ""
)
