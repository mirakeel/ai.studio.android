package com.example.model

object MockData {

    val platforms = listOf(
        PlatformInfo(Platform.INSTAGRAM, "Instagram", true),
        PlatformInfo(Platform.X, "X", true),
        PlatformInfo(Platform.YOUTUBE, "YouTube", true)
    )

    fun getMockDigest(platform: Platform) = Digest(
        id = "d1",
        platform = platform,
        content = "Your ${platform.name} performance is up 15%.",
        timestamp = System.currentTimeMillis()
    )

    fun getMockTrendingTopics() = listOf(
        // Platform X (5 items)
        TrendingTopic("t1", Platform.X, "#ModernCompose", 2400000, 4.2),
        TrendingTopic("t1_2", Platform.X, "K2CompilerRoadmap", 1500000, 2.8),
        TrendingTopic("t1_3", Platform.X, "#KMP_Migration", 1880000, 3.1),
        TrendingTopic("t1_4", Platform.X, "#AndroidStudioLadybug", 1250000, 2.5),
        TrendingTopic("t1_5", Platform.X, "#KotlinK2", 1620000, 3.9),

        // Platform Instagram (5 items)
        TrendingTopic("t2", Platform.INSTAGRAM, "reels_algorithm", 950000, 2.1),
        TrendingTopic("t2_2", Platform.INSTAGRAM, "glassmorphism_widgets", 670000, 1.95),
        TrendingTopic("t2_3", Platform.INSTAGRAM, "gradient_backgrounds", 510000, 1.5),
        TrendingTopic("t2_4", Platform.INSTAGRAM, "material_you_shapes", 820000, 2.4),
        TrendingTopic("t2_5", Platform.INSTAGRAM, "aesthetic_setups", 420000, 1.2),

        // Platform YouTube (5 items)
        TrendingTopic("t3", Platform.YOUTUBE, "JetpackTips", 1600000, 2.4),
        TrendingTopic("t3_2", Platform.YOUTUBE, "RoomBestPractices", 1100000, 1.8),
        TrendingTopic("t3_3", Platform.YOUTUBE, "TypeSafeNavigation", 1350000, 2.2),
        TrendingTopic("t3_4", Platform.YOUTUBE, "M3VisualMakeover", 950000, 1.7),
        TrendingTopic("t3_5", Platform.YOUTUBE, "StateFlowVsSharedFlow", 1250000, 2.0)
    )

    fun getMockUsageStats() = listOf(
        UsageStats(Platform.INSTAGRAM, 45000, 0.05, 120, 840, 3600),
        UsageStats(Platform.X, 32000, 0.08, 45, 315, 1200),
        UsageStats(Platform.YOUTUBE, 55000, 0.03, 90, 630, 2700)
    )

    fun getMockSettings() = UserSettings(
        aiDigestsEnabled = mapOf(
            Platform.INSTAGRAM to true,
            Platform.X to true,
            Platform.YOUTUBE to true
        ),
        notificationStyle = NotificationStyle.IN_APP,
        syncFrequencyMinutes = 60,
        morningBriefingTime = "08:00",
        interestTopics = setOf("Android", "Compose", "Kotlin"),
        activityFilters = setOf(ActivityFilter.POSTS, ActivityFilter.COMMENTS)
    )

    fun generateMockPlatformIntel(platform: Platform): PlatformIntel {
        return when (platform) {
            Platform.X -> PlatformIntel(
                platform = Platform.X,
                overallSentiment = "Highly Energetic / Tech-Focus",
                volumeTrend = "+42% Daily Discussion Volume",
                topCategory = IntelCategory.TECHNOLOGY,
                trendingTopics = listOf(
                    TrendingTopic(
                        id = "inte_t1",
                        platform = Platform.X,
                        title = "#ModernCompose",
                        impressions = 2400000,
                        velocity = 4.2,
                        topicSummary = TopicSummary(
                            mainConcept = "Compose compiler skip-by-default optimizations",
                            summaryText = "The developer space is highly enthusiastic about declarative stability models, showing tremendous engagement surrounding newly released compiler optimizations.",
                            keyBulletPoints = listOf(
                                "Elimination of explicit stability annotations via proactive compilation mapping.",
                                "Strong skipping mode is now on by default, reducing recomposition overhead under high density state loads.",
                                "New performance tooling lets developers inspect layout stability rules directly inside standard Gradle builds."
                            )
                        ),
                        whyTrending = "Key Google Advocate team members announced breakthrough multi-platform benchmarks with up to 35% lag reduction in recursive list views.",
                        mentionedAccounts = listOf(
                            MentionedAccount("@ellie_compose", "Ellie Taylor", "125K followers", "blue"),
                            MentionedAccount("@kotlin_maven", "Suren K.", "84K followers", "silver"),
                            MentionedAccount("@alex_dev_tips", "Alex Tech Tips", "42K followers", "amber")
                        ),
                        contentExamples = listOf(
                            "🔥 Standard skipping is finally active by default in Kotlin 2.0 compiler templates! Tested my list views and skipped recompositions are down to zero. Epic win! #JetpackCompose #AndroidDev",
                            "PSA for Android Devs: Strong skipping is live. Don't waste time decorating model entities, Kotlin K2 analyzes constructor stability out of the box now."
                        ),
                        intelCategory = IntelCategory.TECHNOLOGY
                    ),
                    TrendingTopic(
                        id = "inte_t4",
                        platform = Platform.X,
                        title = "K2CompilerRoadmap",
                        impressions = 1500000,
                        velocity = 2.8,
                        topicSummary = TopicSummary(
                            mainConcept = "Transition speedups with K2 architecture",
                            summaryText = "Discussions focus on build time savings of modern modular libraries compiling with K2, with many projects seeing compile speed improvements.",
                            keyBulletPoints = listOf(
                                "Faster symbol processing reduces build overhead on complex codegraphs.",
                                "Better integration with Kotlin Multiplatform targets eliminates intermediate build steps."
                            )
                        ),
                        whyTrending = "Several large-scale enterprise engineering organizations published retrospective articles on their successful transition to standard K2 builds.",
                        mentionedAccounts = listOf(
                            MentionedAccount("@kotlin", "Kotlin Language", "450K followers", "purple"),
                            MentionedAccount("@gradle", "Gradle", "180K followers", "green")
                        ),
                        contentExamples = listOf(
                            "We migrated our 500-module Jetpack app to K2 compilation last night. Total clean build times went from 8.5 minutes down to 4.8. Truly amazing pipeline gains!",
                            "The compile errors of K2 are so much cleaner. Proactive feedback saves so much development fatigue."
                        ),
                        intelCategory = IntelCategory.TECHNOLOGY
                    )
                )
            )
            Platform.INSTAGRAM -> PlatformIntel(
                platform = Platform.INSTAGRAM,
                overallSentiment = "Aesthetic / Motion Design Boom",
                volumeTrend = "+28% Reels Discovery Impressions",
                topCategory = IntelCategory.CREATIVE,
                trendingTopics = listOf(
                    TrendingTopic(
                        id = "inte_t2",
                        platform = Platform.INSTAGRAM,
                        title = "reels_algorithm",
                        impressions = 950000,
                        velocity = 2.1,
                        topicSummary = TopicSummary(
                            mainConcept = "Micro-education templates and dynamic transitions",
                            summaryText = "Technical educators are maximizing watch loops by designing short, high-contrast, visually pleasing educational video clips containing direct source code snippets.",
                            keyBulletPoints = listOf(
                                "Using bright dynamic color schemas with dark background editor setups raises thumb-stop metrics by 60%.",
                                "Text overlays and dynamic spring animations capture high retention loops.",
                                "The algorithm rewards users sharing repeatable mini-code exercises."
                            )
                        ),
                        whyTrending = "Instagram updated distribution algorithms to favor technical tutorials and aesthetic creators with consistent retention levels.",
                        mentionedAccounts = listOf(
                            MentionedAccount("@android_craft", "Android Craft Studio", "210K followers", "pink"),
                            MentionedAccount("@ui_motion", "UI Motion", "165K followers", "indigo")
                        ),
                        contentExamples = listOf(
                            "💡 Try this spring animation formula next time you build interactive cards! Details and parameters are overlayed below. Let me know what you think!",
                            "Aesthetic dark editor setups coupled with dynamic 10-second transitions are outperforming long voiceovers this week."
                        ),
                        intelCategory = IntelCategory.CREATIVE
                    ),
                    TrendingTopic(
                        id = "inte_t5",
                        platform = Platform.INSTAGRAM,
                        title = "glassmorphism_widgets",
                        impressions = 670000,
                        velocity = 1.95,
                        topicSummary = TopicSummary(
                            mainConcept = "Glassmorphic visual styling on Android components",
                            summaryText = "A massive visual trend showcasing translucent cards and dashboard widgets built with fuzzy render effects and sleek metallic borders.",
                            keyBulletPoints = listOf(
                                "Using background blur multipliers on modern Android 12+ draw caches.",
                                "Elegant high-contrast neon borders paired with sleek dark backdrops for maximum visual depth."
                            )
                        ),
                        whyTrending = "Popular design system releases inspired top creators to publish step-by-step styling tips.",
                        mentionedAccounts = listOf(
                            MentionedAccount("@design_bytes", "Design Bytes", "98K followers", "blue"),
                            MentionedAccount("@clara_codes", "Clara Tech", "140K followers", "orange")
                        ),
                        contentExamples = listOf(
                            "Glassmorphism is back but with dark mode performance-mindful rendering! 🎨 Watch how we computed beautiful real-time blurs.",
                            "Tapping into translucent styling for widget sheets. Perfect blend of accessibility and beautiful aesthetics."
                        ),
                        intelCategory = IntelCategory.CREATIVE
                    )
                )
            )
            Platform.YOUTUBE -> PlatformIntel(
                platform = Platform.YOUTUBE,
                overallSentiment = "Educational / Long-Form Tutorials",
                volumeTrend = "+15% Search & Click-Through Velocity",
                topCategory = IntelCategory.EDUCATION,
                trendingTopics = listOf(
                    TrendingTopic(
                        id = "inte_t3",
                        platform = Platform.YOUTUBE,
                        title = "JetpackTips",
                        impressions = 1600000,
                        velocity = 2.4,
                        topicSummary = TopicSummary(
                            mainConcept = "Type-safe navigation and Room KSP migration guides",
                            summaryText = "Walkthrough channels are recording high click-through videos on the complete migration to compiler-checked navigation parameters, abandoning raw string routing.",
                            keyBulletPoints = listOf(
                                "Type-safe argument passing using KotlinX Serialization annotations.",
                                "KSP migration guides representing massive compilation check speedups for local Room databases.",
                                "Correct implementation patterns for modular nested navigation graphs preventing compilation leaks."
                            )
                        ),
                        whyTrending = "Official Android developer documentation pushed new type-safe route standards as stable, prompting educational tutorials.",
                        mentionedAccounts = listOf(
                            MentionedAccount("DroidAcademy", "Droid Academy Live", "320K subscribers", "red"),
                            MentionedAccount("CodeWithMitch", "Mitch Tabian", "440K subscribers", "gray"),
                            MentionedAccount("PhilippCodes", "Philipp Lackner", "280K subscribers", "dark_gray")
                        ),
                        contentExamples = listOf(
                            "🎬 FULL GUIDE: No more string routes in Jetpack Navigation! Learn step-by-step compile-time type safety.",
                            "Why moving your Room database compiled files from KAPT to KSP will make your test execution and build pipelines twice as fast."
                        ),
                        intelCategory = IntelCategory.EDUCATION
                    ),
                    TrendingTopic(
                        id = "inte_t6",
                        platform = Platform.YOUTUBE,
                        title = "RoomDatabasesBestPractices",
                        impressions = 1100000,
                        velocity = 1.8,
                        topicSummary = TopicSummary(
                            mainConcept = "Database schema migrations and robust caching",
                            summaryText = "In-depth tutorials showcasing offline-first strategies, Room multi-table relations, and robust SQLite schema version checks.",
                            keyBulletPoints = listOf(
                                "Auto-migrations with automated verification test scripts.",
                                "Database inspection tools now showing real-time updates directly in Android studio windows."
                            )
                        ),
                        whyTrending = "Increased focus on fully synchronized offline-first client architectures on critical business and productivity applications.",
                        mentionedAccounts = listOf(
                            MentionedAccount("AndroidDevelopers", "Android Developers official", "1.2M subscribers", "android_green"),
                            MentionedAccount("PhilippCodes", "Philipp Lackner", "280K subscribers", "dark_gray")
                        ),
                        contentExamples = listOf(
                            "Stop writing manual migration raw SQL statements. Jetpack Room's auto-migration takes care of everything safely.",
                            "Architecture blueprints: Creating an elegant local caching repository using flow triggers and Room databases."
                        ),
                        intelCategory = IntelCategory.EDUCATION
                    )
                )
            )
        }
    }
}
