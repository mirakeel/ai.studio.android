package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.viewmodel.SocialDashViewModel

// Mock data models for dynamic updates on tab switching
data class UsageData(
    val instagramShare: Float,
    val xShare: Float,
    val youtubeShare: Float,
    val totalHours: String,
    val totalLabel: String
)

data class TrendIntel(
    val id: String,
    val platformName: String,
    val platformColor: Color,
    val hashtag: String,
    val reachValue: String,
    val increasePercentage: String,
    val sparklinePoints: List<Float>
)

data class PlatformDigest(
    val id: String,
    val name: String,
    val iconName: String,
    val brandColor: Color,
    val digestText: String,
    val unreadCount: Int,
    val fullAlerts: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit = {},
    onPlatformClick: (String) -> Unit = {},
    onTrendClick: (String) -> Unit = {},
    viewModel: SocialDashViewModel = viewModel()
) {
    // 1. Dynamic States
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Today, 1: This Week, 2: This Month
    var isAiDialogOpen by remember { mutableStateOf(false) }
    
    // Hardcoded Mock Data for Usage Tabs
    val todayUsage = UsageData(
        instagramShare = 50f,
        xShare = 30f,
        youtubeShare = 20f,
        totalHours = "3h 42m",
        totalLabel = "Active Today"
    )
    val weekUsage = UsageData(
        instagramShare = 40f,
        xShare = 35f,
        youtubeShare = 25f,
        totalHours = "24h 15m",
        totalLabel = "Active Week"
    )
    val monthUsage = UsageData(
        instagramShare = 50f,
        xShare = 20f,
        youtubeShare = 30f,
        totalHours = "96h 48m",
        totalLabel = "Active Month"
    )

    val currentUsage = when (selectedTab) {
        0 -> todayUsage
        1 -> weekUsage
        else -> monthUsage
    }

    // Mock Trending updates
    val trendingList = remember(viewModel) {
        viewModel.getTrendingTopics().map { trendingTopic ->
            TrendIntel(
                id = trendingTopic.id,
                platformName = trendingTopic.platform.name,
                platformColor = when (trendingTopic.platform) {
                    com.example.model.Platform.INSTAGRAM -> ColorInstagram
                    com.example.model.Platform.X -> ColorX
                    com.example.model.Platform.YOUTUBE -> ColorYoutube
                },
                hashtag = trendingTopic.title,
                reachValue = "${trendingTopic.impressions / 1000}K",
                increasePercentage = "+${trendingTopic.velocity.toInt()}%",
                sparklinePoints = listOf(10f, 25f, 15f, 45f, 30f, 75f, 90f)
            )
        }
    }

    // Platform List
    val platformDigests = remember(viewModel) {
        viewModel.getPlatformDigests().map { platformInfo ->
            PlatformDigest(
                id = platformInfo.platform.name.lowercase(),
                name = platformInfo.displayName,
                iconName = when (platformInfo.platform) {
                    com.example.model.Platform.INSTAGRAM -> "instagram"
                    com.example.model.Platform.X -> "x"
                    com.example.model.Platform.YOUTUBE -> "youtube"
                },
                brandColor = when (platformInfo.platform) {
                    com.example.model.Platform.INSTAGRAM -> ColorInstagram
                    com.example.model.Platform.X -> Color.White
                    com.example.model.Platform.YOUTUBE -> ColorYoutube
                },
                digestText = "Platform status updated.",
                unreadCount = 3,
                fullAlerts = listOf("Alert 1", "Alert 2")
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentIndigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "SocialDash",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            // 2. Time Usage Card (Custom animated donut chart with elegant controls)
            TimeUsageCardSection(
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                usageData = currentUsage
            )

            // 3. AI Digest Card (Gradient glass card with interactive expand details)
            AIDigestCardSection(
                onOpenClick = { isAiDialogOpen = true }
            )

            // 4. Trending Intel Section (Horizontal list with Sparklines & reach indicators)
            TrendingIntelSection(
                trendingList = trendingList,
                onTrendClick = onTrendClick
            )

            // 5. Platform Status List (Detailed lists with custom platform accents)
            PlatformListSection(
                platformDigests = platformDigests,
                onPlatformClick = onPlatformClick
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // AI Digest Premium Detail Overlay Dialog
    if (isAiDialogOpen) {
        AIDigestDetailDialog(
            onDismiss = { isAiDialogOpen = false }
        )
    }
}

@Composable
fun TimeUsageCardSection(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    usageData: UsageData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("time_usage_card"),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Section info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Time Consumption",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = usageData.totalHours,
                        color = AccentIndigoLight,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Custom Styled Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkBackground)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val tabs = listOf("Today", "This Week", "This Month")
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentIndigo else Color.Transparent)
                            .clickable { onTabSelect(index) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Pie Chart and Legend Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Donut/Pie Chart
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Animating sweep ratios elegantly
                    val animIg by animateFloatAsState(targetValue = usageData.instagramShare, animationSpec = spring())
                    val animX by animateFloatAsState(targetValue = usageData.xShare, animationSpec = spring())
                    val animYt by animateFloatAsState(targetValue = usageData.youtubeShare, animationSpec = spring())

                    Canvas(modifier = Modifier.size(110.dp)) {
                        val strokeWidth = 14.dp.toPx()
                        var startAngle = -90f

                        // Slice 1: Instagram
                        val sweepIg = animIg * 360f / 100f
                        drawArc(
                            color = ColorInstagram,
                            startAngle = startAngle,
                            sweepAngle = sweepIg,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweepIg

                        // Slice 2: X
                        val sweepX = animX * 360f / 100f
                        drawArc(
                            color = Color.White,
                            startAngle = startAngle,
                            sweepAngle = sweepX,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweepX

                        // Slice 3: Youtube
                        val sweepYt = animYt * 360f / 100f
                        drawArc(
                            color = ColorYoutube,
                            startAngle = startAngle,
                            sweepAngle = sweepYt,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Inside Label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = usageData.totalHours,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = usageData.totalLabel,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                // Chart Legend Grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    LegendItem("Instagram", "${usageData.instagramShare.toInt()}%", ColorInstagram)
                    LegendItem("X Platform", "${usageData.xShare.toInt()}%", Color.White)
                    LegendItem("YouTube", "${usageData.youtubeShare.toInt()}%", ColorYoutube)
                }
            }
        }
    }
}

@Composable
fun LegendItem(platform: String, percent: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$platform ($percent)",
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AIDigestCardSection(
    onOpenClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_digest_card"),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, SubtleBorderColor)
    ) {
        // Linear Brush Gradient for glow effect
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF312E81), // Deep Blue Indigo
                            Color(0xFF4F46E5), // Vivid Accent Indigo
                            Color(0xFF1E1B4B)  // Dark Indigo Background
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Sparks",
                            tint = Color.Yellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "AI Digest",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    
                    Button(
                        onClick = onOpenClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("open_ai_digest_button")
                    ) {
                        Text(
                            text = "Open",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "Platforms are quiet overall today. Your Reel about layouts is causing a significant spike on Instagram. Schedule high-value postings for X between 7:00 PM and 9:00 PM to maximize active developer visibility.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TrendingIntelSection(
    trendingList: List<TrendIntel>,
    onTrendClick: (String) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Intel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = AccentIndigoLight,
                modifier = Modifier.size(20.dp)
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(trendingList) { trend ->
                TrendingCard(trend = trend, onClick = { onTrendClick(trend.id) })
            }
        }
    }
}

@Composable
fun TrendingCard(trend: TrendIntel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .testTag("trending_card_${trend.id}")
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Dot and platform Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(trend.platformColor)
                    )
                    Text(
                        text = trend.platformName,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentIndigo.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = trend.increasePercentage,
                        color = AccentIndigoLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Keyword Text
            Text(
                text = trend.hashtag,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Sparkline Line plot on custom canvas
            SparklineGraphic(
                points = trend.sparklinePoints,
                color = trend.platformColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            )

            // Volume reach data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reach Volume",
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Text(
                    text = trend.reachValue,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Draw a beautiful custom Sparkline graphical path on Canvas
@Composable
fun SparklineGraphic(
    points: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        
        val width = size.width
        val height = size.height
        
        val maxVal = points.maxOrNull() ?: 1f
        val minVal = points.minOrNull() ?: 0f
        val delta = if (maxVal - minVal == 0f) 1f else (maxVal - minVal)
        
        val stepX = width / (points.size - 1)
        val path = Path()

        points.forEachIndexed { index, point ->
            // Scale and vertical flip
            val normalizedY = (point - minVal) / delta
            val y = height - (normalizedY * height)
            val x = index * stepX
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PlatformListSection(
    platformDigests: List<PlatformDigest>,
    onPlatformClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Active Streams",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            platformDigests.forEach { platform ->
                PlatformRowItem(platform = platform, onClick = { onPlatformClick(platform.id) })
            }
        }
    }
}

@Composable
fun PlatformRowItem(
    platform: PlatformDigest,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("platform_card_${platform.id}")
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Styled platform leading branding icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(platform.brandColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                val iconVector = when (platform.iconName) {
                    "instagram" -> Icons.Default.CameraAlt
                    "x" -> Icons.Default.Close
                    "youtube" -> Icons.Default.PlayArrow
                    else -> Icons.Default.ChatBubble
                }
                
                Icon(
                    imageVector = iconVector,
                    contentDescription = platform.name,
                    tint = if (platform.id == "x") Color.White else platform.brandColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Digest Content details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = platform.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    
                    // Unread Count Badge
                    if (platform.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEF4444)) // Vivid indicator Red
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${platform.unreadCount} Alerts",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = platform.digestText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            // Action Indicator Arrow
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Elegant Detailed AI overlay card dialog
@Composable
fun AIDigestDetailDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, SubtleBorderColor)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Topic bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "AI Intel Insights",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    
                    Card(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextPrimary,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = DarkSurfaceElevated, thickness = 1.dp)

                // List of Bullet points representing AI digest intelligence
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AIDetailBulletPoint("Engagements are trending upwards of 18.5% compared to local weekly averages.", "Instagram reels velocity spiking.")
                    AIDetailBulletPoint("High density active interactions occurring with your pinned Compose threads during afternoon cycles.", "X brand alert indicator is strong.")
                    AIDetailBulletPoint("Action item: Optimize publishing schedules on YouTube to Wednesdays and Saturdays between 4 PM and 7 PM.", "Retention spikes expected.")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Acknowledge Alerts",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AIDetailBulletPoint(alert: String, heading: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(AccentIndigoLight)
        )
        Column {
            Text(
                text = heading,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AccentIndigoLight
            )
            Text(
                text = alert,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

// Elegant Detailed Platform row Dialog
@Composable
fun PlatformDetailDialog(
    platform: PlatformDigest,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, platform.brandColor.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(platform.brandColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val iconVector = when (platform.iconName) {
                                "instagram" -> Icons.Default.CameraAlt
                                "x" -> Icons.Default.Close
                                "youtube" -> Icons.Default.PlayArrow
                                else -> Icons.Default.ChatBubble
                            }
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = if (platform.id == "x") Color.White else platform.brandColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        Text(
                            text = "${platform.name} Streams",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Card(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextPrimary,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = DarkSurfaceElevated, thickness = 1.dp)

                // List of sub notifications for this platform
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    platform.fullAlerts.forEachIndexed { idx, alert ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(platform.brandColor)
                            )
                            Text(
                                text = alert,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = platform.brandColor.copy(alpha = 0.5f).let { if (platform.id == "x") AccentIndigo else platform.brandColor }),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Mark Platform Read",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
