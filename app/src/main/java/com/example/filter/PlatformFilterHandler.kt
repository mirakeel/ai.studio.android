package com.example.filter

import com.example.model.Platform

interface PlatformFilterHandler {
    val platform: Platform
    fun getRules(): List<FilterRule>
    fun addRule(rule: FilterRule)
    fun removeRule(ruleId: String)
    fun toggleRule(ruleId: String, active: Boolean)
    fun compilePipelineScript(): String
}

abstract class BasePlatformFilterHandler(
    override val platform: Platform
) : PlatformFilterHandler {
    
    protected val internalRules = mutableListOf<FilterRule>()

    override fun getRules(): List<FilterRule> = internalRules.toList()

    override fun addRule(rule: FilterRule) {
        internalRules.removeAll { it.id == rule.id }
        internalRules.add(rule)
    }

    override fun removeRule(ruleId: String) {
        internalRules.removeAll { it.id == ruleId }
    }

    override fun toggleRule(ruleId: String, active: Boolean) {
        val index = internalRules.indexOfFirst { it.id == ruleId }
        if (index != -1) {
            val rule = internalRules[index]
            internalRules[index] = rule.copy(isActive = active)
        }
    }

    override fun compilePipelineScript(): String {
        val activeRules = internalRules.filter { it.isActive }
        if (activeRules.isEmpty()) return ""

        val scriptBuilder = StringBuilder()
        scriptBuilder.append("(function() {\n")
        scriptBuilder.append("  console.log('SocialDash: Running filter pipeline for ").append(platform.name).append("');\n")
        
        for (rule in activeRules) {
            scriptBuilder.append("  // Rule: ").append(rule.name).append(" (").append(rule.id).append(")\n")
            scriptBuilder.append("  try {\n")
            
            when (rule.type) {
                FilterRuleType.HIDE_ELEMENT -> {
                    scriptBuilder.append("    document.querySelectorAll('").append(rule.selector).append("').forEach(function(el) {\n")
                    scriptBuilder.append("      el.style.setProperty('display', 'none', 'important');\n")
                    scriptBuilder.append("    });\n")
                }
                FilterRuleType.HIGHLIGHT_ELEMENT -> {
                    val borderStyle = rule.customValue ?: "2px solid #6366F1"
                    scriptBuilder.append("    document.querySelectorAll('").append(rule.selector).append("').forEach(function(el) {\n")
                    scriptBuilder.append("      el.style.setProperty('border', '").append(borderStyle).append("', 'important');\n")
                    scriptBuilder.append("      el.style.setProperty('box-shadow', '0 0 12px rgba(99, 102, 241, 0.45)', 'important');\n")
                    scriptBuilder.append("    });\n")
                }
                FilterRuleType.SIMPLIFY_TYPOGRAPHY -> {
                    scriptBuilder.append("    document.querySelectorAll('").append(rule.selector).append("').forEach(function(el) {\n")
                    scriptBuilder.append("      el.style.setProperty('font-family', 'sans-serif', 'important');\n")
                    scriptBuilder.append("      el.style.setProperty('line-height', '1.6', 'important');\n")
                    scriptBuilder.append("      el.style.setProperty('font-size', '16px', 'important');\n")
                    scriptBuilder.append("    });\n")
                }
                FilterRuleType.CUSTOM_JS -> {
                    if (!rule.customValue.isNullOrBlank()) {
                        scriptBuilder.append("    ").append(rule.customValue).append("\n")
                    }
                }
            }
            scriptBuilder.append("  } catch (e) {\n")
            scriptBuilder.append("    console.error('SocialDash Error executing rule ").append(rule.id).append(":', e);\n")
            scriptBuilder.append("  }\n")
        }
        
        scriptBuilder.append("})();")
        return scriptBuilder.toString()
    }
}

class InstagramFilterHandler : BasePlatformFilterHandler(Platform.INSTAGRAM) {
    init {
        // Default architectural rules for Instagram web element hiding
        addRule(
            FilterRule(
                id = "ig_hide_suggestions",
                name = "Hide Explore & Suggestions Grid",
                description = "Removes side recommendations and suggestion lists to keep focus on the direct feed layout.",
                platform = Platform.INSTAGRAM,
                type = FilterRuleType.HIDE_ELEMENT,
                selector = "div._aamz, div._aany, div[style*='grid-template-columns'] + div"
            )
        )
        addRule(
            FilterRule(
                id = "ig_focus_article",
                name = "Active Feed Focus Border",
                description = "Adds an illuminated indigo indicator around the active social post article element.",
                platform = Platform.INSTAGRAM,
                type = FilterRuleType.HIGHLIGHT_ELEMENT,
                selector = "article, div._ab8w._ab94._ab99._ab9f"
            )
        )
    }
}

class XFilterHandler : BasePlatformFilterHandler(Platform.X) {
    init {
        // Default architectural rules for X (formerly Twitter) distraction isolation
        addRule(
            FilterRule(
                id = "x_hide_sidebar",
                name = "Collapse Right Sidebar Trends",
                description = "Hides the entire right column housing dynamic trends, recommendation listings, and follow suggestions.",
                platform = Platform.X,
                type = FilterRuleType.HIDE_ELEMENT,
                selector = "[data-testid='sidebarColumn']"
            )
        )
        addRule(
            FilterRule(
                id = "x_hide_promoted",
                name = "Hide Ad / Promoted Tweets",
                description = "Filters out tweets flagged with promoted and commercial tags.",
                platform = Platform.X,
                type = FilterRuleType.HIDE_ELEMENT,
                selector = "div[data-testid='cellInnerDiv']:has(span:contains('Promoted')), div[data-testid='placementTracking']"
            )
        )
        addRule(
            FilterRule(
                id = "x_focus_timeline",
                name = "Highlight Main Feed Stream",
                description = "Provides clean negative spacing around the centralized timeline lane.",
                platform = Platform.X,
                type = FilterRuleType.HIGHLIGHT_ELEMENT,
                selector = "[data-testid='primaryColumn']",
                customValue = "1px solid #1DA1F2"
            )
        )
    }
}

class YouTubeFilterHandler : BasePlatformFilterHandler(Platform.YOUTUBE) {
    init {
        // Default architectural rules for YouTube flow-state enhancement
        addRule(
            FilterRule(
                id = "yt_hide_sidebar_recommendations",
                name = "Hide Recommendations Panel",
                description = "Hides the infinite side recommendation column during video playback to eliminate clicking on suggested videos.",
                platform = Platform.YOUTUBE,
                type = FilterRuleType.HIDE_ELEMENT,
                selector = "#related, ytd-watch-next-secondary-results-renderer"
            )
        )
        addRule(
            FilterRule(
                id = "yt_hide_comments",
                name = "Mute Comments Stream",
                description = "Collapses the visual user comments segment below the primary player feed structure.",
                platform = Platform.YOUTUBE,
                type = FilterRuleType.HIDE_ELEMENT,
                selector = "#comments"
            )
        )
        addRule(
            FilterRule(
                id = "yt_focus_player",
                name = "Cinematic Border for Player",
                description = "Puts a sleek indicator frame around the video container stream.",
                platform = Platform.YOUTUBE,
                type = FilterRuleType.HIGHLIGHT_ELEMENT,
                selector = "#movie_player, .html5-video-player",
                customValue = "3px solid #FF0000"
            )
        )
    }
}
