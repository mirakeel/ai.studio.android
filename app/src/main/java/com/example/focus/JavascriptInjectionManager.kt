package com.example.focus

import android.util.Log
import com.example.model.FilterType
import com.example.model.FocusModeSettings
import com.example.model.Platform

/**
 * JavascriptInjectionManager manages and compiles optimized injection rules for platform webviews.
 */
class JavascriptInjectionManager private constructor() {

    private val platformScripts = mutableMapOf<Platform, PlatformInjectionScript>()

    init {
        // Architecture only: Instantiate empty or placeholder selectors to adhere to "do not write platform-specific selectors yet"
        platformScripts[Platform.INSTAGRAM] = PlatformInjectionScript(
            platform = Platform.INSTAGRAM,
            name = "Instagram focus system",
            filterSelectors = mapOf(
                FilterType.HIDE_REELS to listOf(
                    "a[href*='/reels/']",
                    "svg[aria-label*='Reels']",
                    "svg[aria-label*='reels']",
                    "div:has(> a[href*='/reels/'])",
                    "span:has(> a[href*='/reels/'])"
                ),
                FilterType.HIDE_EXPLORE to listOf(
                    "a[href*='/explore/']",
                    "svg[aria-label*='Explore']",
                    "svg[aria-label*='explore']",
                    "div:has(> a[href*='/explore/'])",
                    "span:has(> a[href*='/explore/'])"
                ),
                FilterType.HIDE_SUGGESTED_POSTS to listOf(
                    "div._a26a",
                    "div[class*='suggested-posts']"
                ),
                FilterType.HIDE_SUGGESTED_ACCOUNTS to listOf(
                    "div._a9-z",
                    "div._a993",
                    "div[class*='suggested']",
                    "section[class*='suggested']"
                )
            ),
            globalHideSelectors = emptyList(),
            customJs = """
                (function() {
                    console.log('FocusMode: Starting Instagram continuous self-retry POC integration');
                    var lastCount = 0;
                    
                    function runPoCEngine() {
                        try {
                            var totalRemoved = 0;
                            var itemsFound = [];

                            // 1. Hide Explore navigation elements
                            var exploreElements = document.querySelectorAll("a[href*='/explore/'], svg[aria-label*='Explore'], svg[aria-label*='explore']");
                            exploreElements.forEach(function(el) {
                                var container = el.closest('div[style*="column"]') || el.closest('span') || el.closest('li') || el;
                                if (container.style.display !== 'none') {
                                    container.style.setProperty('display', 'none', 'important');
                                    container.style.setProperty('visibility', 'hidden', 'important');
                                    totalRemoved++;
                                    itemsFound.push("Explore Entry");
                                }
                            });

                            // 2. Hide Reels navigation elements
                            var reelsElements = document.querySelectorAll("a[href*='/reels/'], svg[aria-label*='Reels'], svg[aria-label*='reels']");
                            reelsElements.forEach(function(el) {
                                var container = el.closest('div[style*="column"]') || el.closest('span') || el.closest('li') || el;
                                if (container.style.display !== 'none') {
                                    container.style.setProperty('display', 'none', 'important');
                                    container.style.setProperty('visibility', 'hidden', 'important');
                                    totalRemoved++;
                                    itemsFound.push("Reels Entry");
                                }
                            });

                            // 3 & 4. Hide Suggested accounts and Suggested posts modules via text content scanner
                            var queryKeywords = ["Suggested for you", "Suggestions for you", "Suggested Posts", "Suggested posts"];
                            var domElements = document.querySelectorAll("span, h4, div, p");
                            domElements.forEach(function(el) {
                                if (el.childNodes.length === 1 && el.childNodes[0].nodeType === 3) {
                                    var nodeText = el.textContent.trim().toLowerCase();
                                    queryKeywords.forEach(function(keyword) {
                                        if (nodeText === keyword.toLowerCase()) {
                                            var parentCard = el.closest('article') || el.closest('div._a9-z') || el.closest('div._a993') || el.closest('section') || el.parentElement;
                                            if (parentCard && parentCard.style.display !== 'none') {
                                                parentCard.style.setProperty('display', 'none', 'important');
                                                parentCard.style.setProperty('visibility', 'hidden', 'important');
                                                parentCard.style.setProperty('height', '0', 'important');
                                                parentCard.style.setProperty('overflow', 'hidden', 'important');
                                                totalRemoved++;
                                                itemsFound.push("Suggested (" + keyword + ")");
                                            }
                                        }
                                    });
                                }
                            });

                            if (totalRemoved > 0 && totalRemoved !== lastCount) {
                                lastCount = totalRemoved;
                                console.log("FocusMode: Subscribed counts of removed IG distractions updated: " + totalRemoved);
                                if (window.FocusModeBridge && typeof window.FocusModeBridge.logHiddenCount === 'function') {
                                    window.FocusModeBridge.logHiddenCount('Instagram', totalRemoved, itemsFound.join(", "));
                                }
                            }
                        } catch (err) {
                            console.error("FocusMode POC Javascript error: ", err);
                            if (window.FocusModeBridge && typeof window.FocusModeBridge.logError === 'function') {
                                window.FocusModeBridge.logError(err.toString());
                            }
                        }
                    }

                    // Run initial apply
                    runPoCEngine();

                    // Observe DOM changes dynamically to retry script when page loads new scrolling cards
                    try {
                        var focusMutationObserver = new MutationObserver(function() {
                            runPoCEngine();
                        });
                        focusMutationObserver.observe(document.body, { childList: true, subtree: true });
                        console.log("FocusMode: Instagram dynamic MutationObserver established successfully.");
                    } catch (observerError) {
                        console.error("FocusMode: Failed to bind observer, falling back to interval checks. Error: ", observerError);
                        setInterval(runPoCEngine, 2000);
                    }
                })();
            """.trimIndent()
        )

        platformScripts[Platform.X] = PlatformInjectionScript(
            platform = Platform.X,
            name = "X focus system",
            filterSelectors = mapOf(
                FilterType.HIDE_FOR_YOU to listOf(".placeholder-x-foryou"),
                FilterType.HIDE_TRENDING to listOf(".placeholder-x-trending"),
                FilterType.HIDE_SUGGESTED_USERS to listOf(".placeholder-x-suggested")
            ),
            globalHideSelectors = emptyList(),
            customJs = "console.log('X deep focus engine initialized.');"
        )

        platformScripts[Platform.YOUTUBE] = PlatformInjectionScript(
            platform = Platform.YOUTUBE,
            name = "YouTube focus system",
            filterSelectors = mapOf(
                FilterType.HIDE_SHORTS to listOf(".placeholder-yt-shorts"),
                FilterType.HIDE_HOME_RECOMMENDATIONS to listOf(".placeholder-yt-home-recs"),
                FilterType.HIDE_SUGGESTED_VIDEOS to listOf(".placeholder-yt-suggested-videos"),
                FilterType.HIDE_END_SCREEN_RECOMMENDATIONS to listOf(".placeholder-yt-endscreen")
            ),
            globalHideSelectors = emptyList(),
            customJs = "console.log('YouTube deep focus engine initialized.');"
        )
    }

    /**
     * Retrieves the base integration script parameters for a given platform.
     */
    fun getScriptForPlatform(platform: Platform): PlatformInjectionScript? {
        return platformScripts[platform]
    }

    /**
     * Compiles custom CSS rules and custom JS dynamically based on active filters in [settings].
     */
    fun compileFocusScript(platform: Platform, settings: FocusModeSettings): String {
        // If Focus Mode is fully disabled globally, return blank script
        if (!settings.isEnabled) {
            return ""
        }

        val platformScript = platformScripts[platform] ?: return ""
        val platformFilters = settings.platformFilters[platform]
        
        val selectorsToHide = mutableListOf<String>()
        // Add always-hidden global selectors
        selectorsToHide.addAll(platformScript.globalHideSelectors)

        // Add selectors for active filters
        platformFilters?.enabledFilters?.forEach { (filterType, isEnabled) ->
            if (isEnabled) {
                val selectors = platformScript.filterSelectors[filterType]
                if (selectors != null) {
                    selectorsToHide.addAll(selectors)
                }
            }
        }

        val compiledScript = StringBuilder()
        compiledScript.append("(function() {\n")
        compiledScript.append("  console.log('FocusMode: Executing platform rules for ").append(platform.name).append("');\n")

        if (selectorsToHide.isNotEmpty()) {
            val joinedSelectors = selectorsToHide.joinToString(", ")
            
            // 1. Inject or update css stylesheet to completely suppress layouts of designated elements
            compiledScript.append("  try {\n")
            compiledScript.append("    var focusStyle = document.getElementById('socialdash-focus-mode');\n")
            compiledScript.append("    if (!focusStyle) {\n")
            compiledScript.append("      focusStyle = document.createElement('style');\n")
            compiledScript.append("      focusStyle.id = 'socialdash-focus-mode';\n")
            compiledScript.append("      document.head.appendChild(focusStyle);\n")
            compiledScript.append("    }\n")
            compiledScript.append("    focusStyle.textContent = '")
                .append(joinedSelectors.replace("'", "\\'"))
                .append(" { display: none !important; visibility: hidden !important; height: 0 !important; overflow: hidden !important; }';\n")
            compiledScript.append("    console.log('FocusMode: Focus styles injected/updated successfully.');\n")
            compiledScript.append("  } catch(e) {\n")
            compiledScript.append("    console.error('FocusMode style injection error:', e);\n")
            compiledScript.append("  }\n")

            // 2. Perform aggressive DOM removals on identified selectors to fully clear elements from memory
            compiledScript.append("  try {\n")
            compiledScript.append("    var removedCount = 0;\n")
            compiledScript.append("    document.querySelectorAll('").append(joinedSelectors.replace("'", "\\'")).append("').forEach(function(el) {\n")
            compiledScript.append("      el.remove();\n")
            compiledScript.append("      removedCount++;\n")
            compiledScript.append("    });\n")
            compiledScript.append("    console.log('FocusMode: Proactively removed ' + removedCount + ' elements from DOM.');\n")
            compiledScript.append("  } catch(e) {\n")
            compiledScript.append("    console.error('FocusMode DOM removal error:', e);\n")
            compiledScript.append("  }\n")
        }

        // 3. Inject and run custom javascript block
        if (platformScript.customJs.isNotBlank()) {
            compiledScript.append("  try {\n")
            compiledScript.append("    ").append(platformScript.customJs).append("\n")
            compiledScript.append("  } catch(e) {\n")
            compiledScript.append("    console.error('FocusMode Custom JS execution error:', e);\n")
            compiledScript.append("  }\n")
        }

        compiledScript.append("})();")
        return compiledScript.toString()
    }

    companion object {
        @Volatile
        private var instance: JavascriptInjectionManager? = null

        fun getInstance(): JavascriptInjectionManager {
            return instance ?: synchronized(this) {
                instance ?: JavascriptInjectionManager().also { instance = it }
            }
        }
    }
}
