package com.example.ui

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.*
import kotlinx.coroutines.delay

private data class PlatformBranding(
    val id: String,
    val name: String,
    val brandColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val url: String,
    val defaultDigest: String,
    val initialAiAnalysis: String,
    val regeneratedAiAnalysis: String
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlatformScreen(
    platformId: String,
    onBackClick: () -> Unit
) {
    // 1. Map platform-specific details & mock analytics
    val platformInfo = remember(platformId) {
        when (platformId.lowercase()) {
            "ig" -> PlatformBranding(
                id = "ig",
                name = "Instagram",
                brandColor = ColorInstagram,
                icon = Icons.Default.CameraAlt,
                url = "https://www.instagram.com",
                defaultDigest = "Weekend Reel surpassed typical benchmark reach. Profile visits spiked +34.2%. Video watch-duration is steady.",
                initialAiAnalysis = "Primary audience response peaks in the first 3 seconds containing visual code tutorials. Demographics cluster dynamically between ages 18-34.",
                regeneratedAiAnalysis = "Visual coding assets show 2.4x standard conversion multipliers. Real-time recommendation: Schedule your subsequent layout highlight reel within the next 3 hours to capture US afterwork traffic."
            )
            "x" -> PlatformBranding(
                id = "x",
                name = "X Platform",
                brandColor = Color.White,
                icon = Icons.Default.Close,
                url = "https://x.com",
                defaultDigest = "Your Jetpack Compose Canvas framework highlight is trending in DevCircle. Overall interaction indexes at 18.5%.",
                initialAiAnalysis = "Technical threads receive heightened retention rates. Active code snippet copy triggers are high (+45% week-over-week).",
                regeneratedAiAnalysis = "Public sentiment indices show 92% positive builder approval. Recommend pinning the standard repo link today to capitalize on viral impressions."
            )
            "yt" -> PlatformBranding(
                id = "yt",
                name = "YouTube",
                brandColor = ColorYoutube,
                icon = Icons.Default.PlayArrow,
                url = "https://www.youtube.com",
                defaultDigest = "Channel total subscriber counts breached 5,000. Real-time stats trace higher-than-average retention on database videos.",
                initialAiAnalysis = "Main visual thumbnail CTR is steady at 9.2%. Search traffic drives 45% of discovery feeds, suggesting robust index descriptors.",
                regeneratedAiAnalysis = "Engagement spikes dramatically on Community visual polls. Optimal publishing window suggested: Wednesdays between 4:00 PM and 7:00 PM."
            )
            else -> PlatformBranding(
                id = "unknown",
                name = "Unknown Feed",
                brandColor = Color.Gray,
                icon = Icons.Default.Info,
                url = "https://www.google.com",
                defaultDigest = "Data source unavailable or undefined for standard profile ID.",
                initialAiAnalysis = "AI pipeline inactive. Connect a designated account API descriptor.",
                regeneratedAiAnalysis = "Awaiting live API endpoints in subsequent setup phases."
            )
        }
    }

    // 2. Control states for WebView reloads and dynamic animations
    var reloadKey by remember { mutableIntStateOf(0) }
    var lastReloadKey by remember { mutableIntStateOf(0) }
    var lastLoadedUrl by remember { mutableStateOf("") }
    var isReloading by remember { mutableStateOf(false) }

    // Rotate reload button animation
    val reloadRotation by animateFloatAsState(
        targetValue = if (isReloading) 360f else 0f,
        animationSpec = spring(),
        finishedListener = { isReloading = false }
    )

    // 3. AI Side panel states
    var isAiPanelOpen by remember { mutableStateOf(false) }
    var currentTimestamp by remember { mutableStateOf("May 30, 16:15 UTC") }
    var aiAnalysisText by remember { mutableStateOf(platformInfo.initialAiAnalysis) }
    var isGeneratingAi by remember { mutableStateOf(false) }

    // Trigger AI compilation delay sequence
    if (isGeneratingAi) {
        LaunchedEffect(Unit) {
            delay(1500) // Aesthetic calculation delay
            aiAnalysisText = platformInfo.regeneratedAiAnalysis
            val currentTime = java.text.SimpleDateFormat("MMM dd, HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            currentTimestamp = "$currentTime UTC"
            isGeneratingAi = false
        }
    }

    // Synchronize UI content text on platform code reload
    LaunchedEffect(platformInfo) {
        aiAnalysisText = platformInfo.initialAiAnalysis
        currentTimestamp = "May 30, 16:15 UTC"
    }

    // Main layout with overlapping Side Panel
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(platformInfo.brandColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = platformInfo.icon,
                                    contentDescription = platformInfo.name,
                                    tint = if (platformId.lowercase() == "x") Color.White else platformInfo.brandColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = platformInfo.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("platform_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                isReloading = true
                                reloadKey++
                            },
                            modifier = Modifier
                                .testTag("platform_refresh_button")
                                .graphicsLayer(rotationZ = reloadRotation)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload Feed",
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
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("AI", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Panel Trigger") },
                    onClick = { isAiPanelOpen = true },
                    containerColor = AccentIndigo,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("platform_ai_fab")
                )
            },
            containerColor = DarkBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Page loading visual feedback
                    if (isReloading) {
                        LinearProgressIndicator(
                            color = AccentIndigo,
                            trackColor = Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                        )
                    }

                    // Native WebView embedding matching wrapped specifications
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                        super.onPageStarted(view, url, favicon)
                                        isReloading = true
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        isReloading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .testTag("platform_webview"),
                        update = { webView ->
                            if (lastLoadedUrl != platformInfo.url) {
                                webView.loadUrl(platformInfo.url)
                                lastLoadedUrl = platformInfo.url
                            }
                            if (lastReloadKey != reloadKey) {
                                webView.reload()
                                lastReloadKey = reloadKey
                            }
                        }
                    )
                }
            }
        }

        // --- Custom Polished Modal Side Panel (Drawer Style) ---
        
        // Background Dim Click Scrim overlay
        AnimatedVisibility(
            visible = isAiPanelOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isAiPanelOpen = false
                    }
            )
        }

        // Animated Right Slide Drawer
        AnimatedVisibility(
            visible = isAiPanelOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.82f)
                    .background(DarkSurfaceElevated)
                    .border(
                        BorderStroke(1.dp, SubtleBorderColor),
                        shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Absorb clicks to prevent dismiss */ }
                    .padding(20.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Title Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AccentIndigoLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Advisory Panel",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            IconButton(
                                onClick = { isAiPanelOpen = false },
                                modifier = Modifier.testTag("platform_ai_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Panel",
                                    tint = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = SubtleBorderColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Scrollable insight panel content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Section: Feed Digest Card
                            Column {
                                Text(
                                    text = "PLATFORM STATS DIGEST",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = BorderStroke(1.dp, SubtleBorderColor.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = platformInfo.defaultDigest,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }

                            // Section: Intel Action Insights Card
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AI REAL-TIME INTELLIGENCE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (isGeneratingAi) AccentIndigoLight else Color.Green)
                                        )
                                        Text(
                                            text = if (isGeneratingAi) "ANALYZING" else "SYNCED",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isGeneratingAi) AccentIndigoLight else TextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                    border = BorderStroke(1.dp, SubtleBorderColor)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        if (isGeneratingAi) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    color = AccentIndigo,
                                                    modifier = Modifier.size(24.dp),
                                                    strokeWidth = 3.dp
                                                )
                                                Text(
                                                    text = "Synthesizing visual graphs...",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMuted
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = aiAnalysisText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextPrimary,
                                                lineHeight = 21.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.3f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Calculated At",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted
                                            )
                                            Text(
                                                text = currentTimestamp,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Real-Time Analysis Trigger Button at base of side panel
                    Button(
                        onClick = { isGeneratingAi = true },
                        enabled = !isGeneratingAi,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentIndigo,
                            disabledContainerColor = AccentIndigo.copy(alpha = 0.4f),
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("platform_generate_analysis_button")
                    ) {
                        if (isGeneratingAi) {
                            Text(text = "Formulating Advisory...", fontWeight = FontWeight.Bold)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Generate Real-Time Analysis",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
