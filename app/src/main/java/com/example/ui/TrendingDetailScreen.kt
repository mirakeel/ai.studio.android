package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

private data class TrendingDetailData(
    val id: String,
    val topicTitle: String,
    val platformName: String,
    val platformColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val reachValue: String,
    val velocity: String,
    val whatItIs: String,
    val whyTrending: String,
    val perspectives: List<Pair<String, String>>,
    val followedMentions: List<FollowedAccount>,
    val sparklinePoints: List<Float>
)

private data class FollowedAccount(
    val handle: String,
    val displayName: String,
    val initialLetter: String,
    val quote: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingDetailScreen(
    trendId: String,
    onBackClick: () -> Unit
) {
    // 1. Resolve Trend data dynamically from ID
    val trendData = remember(trendId) {
        when (trendId.lowercase()) {
            "t1" -> TrendingDetailData(
                id = "t1",
                topicTitle = "#ModernCompose",
                platformName = "X Platform",
                platformColor = ColorX,
                icon = Icons.Default.Close,
                reachValue = "2.1M Impressions",
                velocity = "+340% Velocity",
                whatItIs = "A viral community discussion celebrating the latest Jetpack Compose compiler enhancements, standardizing strong skipping mode, explicit stability declarations, and performance debugging metrics.",
                whyTrending = "Key Android Developer Advocates and Google engineering teams released benchmarks showing up to 30% reduction in recomposition overhead for deeply nested, high-density layouts.",
                perspectives = listOf(
                    "Performance Optimization" to "Developers report dramatic frames-per-second improvements by enabling strong skipping mode in existing legacy Compose codebases.",
                    "Tooling & Lint Support" to "The newly integrated compiler rules raise proactive flags around unstable parameter references right at build time instead of runtime.",
                    "Explicit Stability Guards" to "An increasing industry push to adopt simple immutable data formats rather than relying on heavy manual @Stable declarations."
                ),
                followedMentions = listOf(
                    FollowedAccount("@ellie_compose", "Ellie Dev", "E", "Strong skipping is an industry game-changer. My list rendering speed essentially doubled!"),
                    FollowedAccount("@kotlin_maven", "Maxim Kotlin", "K", "K2 migration feels seamless now. Kudos to the tooling teams working on these compiler plugins."),
                    FollowedAccount("@alex_dev_tips", "Alex Tech", "A", "Just published standard snippets for custom benchmark tests in Compose. Must check!")
                ),
                sparklinePoints = listOf(10f, 25f, 15f, 45f, 30f, 75f, 90f)
            )
            "t2" -> TrendingDetailData(
                id = "t2",
                topicTitle = "reels_algorithm",
                platformName = "Instagram",
                platformColor = ColorInstagram,
                icon = Icons.Default.CameraAlt,
                reachValue = "890K Impressions",
                velocity = "+185% Velocity",
                whatItIs = "A high-momentum trend showcasing technical multi-part reels focusing on real-time Android animations, UI transitions, and micro-interactions constructed under 15 seconds.",
                whyTrending = "Recent adjustments in product search and feed weights heavily reward technical instruction creators who capture repeat watch-count loops with text overlays.",
                perspectives = listOf(
                    "Immediate Visual Value" to "Displaying the working functional UI within the first two seconds yields 4x higher audience retention metrics.",
                    "Acoustic Engagement" to "Low-fi background coding tracks are showing the highest positive correlation with repeat-count loop indicators.",
                    "Discoverability SEO" to "Dynamic screen captures overlaid with concise captions outperform standard, generic descriptive copy blocks."
                ),
                followedMentions = listOf(
                    FollowedAccount("@android_craft", "Android Craft", "C", "Quick video showcasing spring bounce physics drew +15k profile visits overnight!"),
                    FollowedAccount("@ui_motion", "UI Motion Studio", "M", "Instagram's new algorithm is definitely favoring hyper-focused short Android tutorials."),
                    FollowedAccount("@clara_codes", "Clara Tech", "C", "If you are not framing your UI tips with high contrast visual hooks now, you are literally losing outreach.")
                ),
                sparklinePoints = listOf(20f, 30f, 50f, 40f, 65f, 60f, 85f)
            )
            "t3" -> TrendingDetailData(
                id = "t3",
                topicTitle = "JetpackTips",
                platformName = "YouTube",
                platformColor = ColorYoutube,
                icon = Icons.Default.PlayArrow,
                reachValue = "1.4M Impressions",
                velocity = "+210% Velocity",
                whatItIs = "A robust surge in long-form walkthrough tutorials focusing on Kotlin Serialization integration within Jetpack Navigation and advanced Room Local Storage setups.",
                whyTrending = "The official promotion of compiler-safe navigation configurations led to multiple educators releasing technical guide videos synchronously.",
                perspectives = listOf(
                    "Type-Safe Core Routes" to "Moving completely away from standard string route construction prevents runtime crash scenarios when moving deep data payloads.",
                    "KSP Compilation Checkups" to "Integration with Kotlin Symbol Processing for Room compilation safeguards SQL queries ahead of deployment.",
                    "Graph Modularization" to "Structuring nested navigation graphs correctly allows teams of scale to build parallel layouts without code conflicts."
                ),
                followedMentions = listOf(
                    FollowedAccount("DroidAcademy", "Droid Academy", "D", "Our latest full tutorial on modern Compose Nav boasts our highest direct Click-Through Rate this year!"),
                    FollowedAccount("CodeWithMitch", "Mitch Tech", "M", "Real database code with type-safe arguments makes modern apps incredibly stable. Highly recommended."),
                    FollowedAccount("PhilippCodes", "Philipp L.", "P", "Using Room with KSP is basically standard protocol for any reliable persistence layer nowadays.")
                ),
                sparklinePoints = listOf(5f, 12f, 28f, 22f, 49f, 62f, 78f)
            )
            else -> TrendingDetailData(
                id = "unknown",
                topicTitle = "General Analytics",
                platformName = "Combined Feeds",
                platformColor = AccentIndigo,
                icon = Icons.Default.Info,
                reachValue = "450K Impressions",
                velocity = "+45% Velocity",
                whatItIs = "General data fluctuations regarding technical outreach indices across remaining active feeds.",
                whyTrending = "Gradual weekly engagement shifts reflecting standard user activity changes across key zones.",
                perspectives = listOf(
                    "Metric Variance" to "Slight shifts in user reading hours do not significantly disrupt deep analytics baselines."
                ),
                followedMentions = emptyList(),
                sparklinePoints = listOf(50f, 48f, 52f, 49f, 55f, 51f, 58f)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Story Intelligence",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Real-Time Trend Analytics",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("trending_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("trending_detail_container"),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // --- HEADER TOPIC CARD WITH DYNAMIC LIGHT SPARKLINE BACKGROUND ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, SubtleBorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Platform tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(trendData.platformColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = trendData.icon,
                                    contentDescription = null,
                                    tint = if (trendData.platformName == "X Platform") Color.White else trendData.platformColor,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = trendData.platformName.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 1.sp
                            )
                        }

                        // Hashtag Title
                        Text(
                            text = trendData.topicTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Multiplier metrics status row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Impression Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(BorderStroke(1.dp, SubtleBorderColor), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("trending_reach_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = AccentIndigoLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = trendData.reachValue,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Growth Acceleration Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(BorderStroke(1.dp, SubtleBorderColor), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("trending_velocity_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = Color.Green,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = trendData.velocity,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Green
                                    )
                                }
                            }
                        }
                    }

                    // Decorative Trend Sparkline in Upper-Right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(75.dp, 45.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val points = trendData.sparklinePoints
                            if (points.size > 1) {
                                val path = Path()
                                val maxVal = points.maxOrNull() ?: 1f
                                val minVal = points.minOrNull() ?: 0f
                                val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal
                                val widthStep = size.width / (points.size - 1)

                                points.forEachIndexed { index, y ->
                                    val xCoord = index * widthStep
                                    val yNormalized = (y - minVal) / range
                                    val yCoord = size.height - (yNormalized * size.height)
                                    if (index == 0) {
                                        path.moveTo(xCoord, yCoord)
                                    } else {
                                        path.lineTo(xCoord, yCoord)
                                    }
                                }

                                drawPath(
                                    path = path,
                                    color = trendData.platformColor,
                                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }
            }

            // --- WHAT IT IS CARD ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AccentIndigoLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "WHAT IT IS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = trendData.whatItIs,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 22.sp
                    )
                }
            }

            // --- WHY IT IS TRENDING CARD ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "WHY IT'S TRENDING",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = trendData.whyTrending,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 22.sp
                    )
                }
            }

            // --- KEY PERSPECTIVES SECTION ---
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LibraryBooks,
                        contentDescription = null,
                        tint = AccentIndigoLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "KEY PERSPECTIVES",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                trendData.perspectives.forEachIndexed { idx, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = BorderStroke(1.dp, SubtleBorderColor.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(trendData.platformColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.first,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.second,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // --- FOLLOWED ACCOUNTS MENTIONING THIS TOPIC ---
            if (trendData.followedMentions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = AccentIndigoLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "FOLLOWED CREATORS ENGAGING",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Horizontal list of followed profiles with quotes
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        trendData.followedMentions.forEach { account ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = BorderStroke(1.dp, SubtleBorderColor.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular Initial Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        DarkSurfaceElevated,
                                                        trendData.platformColor.copy(alpha = 0.15f)
                                                    )
                                                )
                                            )
                                            .border(1.5.dp, trendData.platformColor.copy(alpha = 0.6f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = account.initialLetter,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = account.displayName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = account.handle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"${account.quote}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
