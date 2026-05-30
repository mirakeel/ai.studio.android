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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.model.*
import com.example.viewmodel.RequestState
import com.example.viewmodel.SocialDashViewModel
import com.example.viewmodel.FocusModeViewModel
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

private data class WebViewState(val url: String, val reloadKey: Int)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlatformScreen(
    platformId: String,
    onBackClick: () -> Unit,
    viewModel: SocialDashViewModel = viewModel()
) {
    // 1. Map platform-specific details & mock analytics
    val platformInfo = remember(platformId) {
        when (platformId.lowercase()) {
            "ig", "instagram" -> PlatformBranding(
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
            "yt", "youtube" -> PlatformBranding(
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
    var isReloading by remember { mutableStateOf(false) }

    // Rotate reload button animation
    val reloadRotation by animateFloatAsState(
        targetValue = if (isReloading) 360f else 0f,
        animationSpec = spring(),
        finishedListener = { isReloading = false }
    )

    // 3. AI Side panel states, ViewModel integration, and Focus Mode
    var isAiPanelOpen by remember { mutableStateOf(false) }
    val aiAnalysisState by viewModel.aiAnalysisState.collectAsStateWithLifecycle()
    val latestDigest by viewModel.latestDigest.collectAsStateWithLifecycle()
    
    val focusModeViewModel: FocusModeViewModel = viewModel()
    val focusModeSettings by focusModeViewModel.focusModeSettings.collectAsStateWithLifecycle()

    val platformEnum = remember(platformId) {
        when (platformId.lowercase()) {
            "ig", "instagram" -> Platform.INSTAGRAM
            "x", "twitter" -> Platform.X
            "yt", "youtube" -> Platform.YOUTUBE
            else -> Platform.INSTAGRAM
        }
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.example.session.SessionManager.getInstance(context) }
    var isLogged by remember(platformEnum) {
        mutableStateOf(sessionManager.isSessionActive(platformEnum))
    }

    LaunchedEffect(platformEnum, reloadKey) {
        delay(500) // Small delay to allow cookies to settle
        isLogged = sessionManager.isSessionActive(platformEnum)
    }

    // Fetch digest on load and reset analysis
    LaunchedEffect(platformId) {
        viewModel.fetchLatestDigest(platformId)
        viewModel.resetAiAnalysis()
    }

    // Heartbeat loop: Send signal every 60 seconds while viewing platform
    LaunchedEffect(platformId) {
        while (true) {
            delay(60_000)
            viewModel.sendHeartbeat(platformId)
        }
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
                        if (isLogged) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF10B981)
                                )
                            }
                            IconButton(
                                onClick = {
                                    sessionManager.logout(platformEnum) {
                                        isLogged = false
                                        reloadKey++
                                    }
                                },
                                modifier = Modifier.testTag("platform_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = Color.Red.copy(alpha = 0.8f)
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Inactive",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }

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
                    PlatformRealWebView(
                        platformId = platformId,
                        platformUrl = platformInfo.url,
                        reloadKey = reloadKey,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
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
                                            text = latestDigest?.content ?: platformInfo.defaultDigest,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            lineHeight = 20.sp
                                        )
                                        
                                        latestDigest?.let { digest ->
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = java.text.SimpleDateFormat("MMM dd, HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(digest.timestamp)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted
                                            )
                                        }
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
                                        val isGenerating = aiAnalysisState is RequestState.Loading
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (isGenerating) AccentIndigoLight else Color.Green)
                                        )
                                        Text(
                                            text = if (isGenerating) "ANALYZING" else "SYNCED",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isGenerating) AccentIndigoLight else TextSecondary,
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
                                        when (val state = aiAnalysisState) {
                                            is RequestState.Loading -> {
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
                                            }
                                            is RequestState.Success -> {
                                                Text(
                                                    text = state.data.analysisText,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextPrimary,
                                                    lineHeight = 21.sp
                                                )
                                                
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
                                                        text = java.text.SimpleDateFormat("MMM dd, HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(state.data.timestamp)),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                            is RequestState.Error -> {
                                                Text(
                                                    text = state.message,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.error,
                                                    lineHeight = 21.sp
                                                )
                                            }
                                            else -> {
                                                Text(
                                                    text = platformInfo.initialAiAnalysis,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextPrimary,
                                                    lineHeight = 21.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Real-Time Analysis Trigger Button at base of side panel
                    Button(
                        onClick = { viewModel.generateRealTimeAnalysis(platformId) },
                        enabled = aiAnalysisState !is RequestState.Loading,
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
                        if (aiAnalysisState is RequestState.Loading) {
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

@Composable
private fun PlatformRealWebView(
    platformId: String,
    platformUrl: String,
    reloadKey: Int,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val platformEnum = remember(platformId) {
        when (platformId.lowercase()) {
            "ig", "instagram" -> Platform.INSTAGRAM
            "x", "twitter" -> Platform.X
            "yt", "youtube" -> Platform.YOUTUBE
            else -> Platform.INSTAGRAM
        }
    }

    val webViewManager = remember { com.example.session.PlatformWebViewManager.getInstance(context) }
    var runningWebView by remember(platformEnum) { mutableStateOf<android.webkit.WebView?>(null) }

    val focusModeViewModel: com.example.viewmodel.FocusModeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val focusModeSettings by focusModeViewModel.focusModeSettings.collectAsStateWithLifecycle()

    // Recreate/Re-load WebView if platform changes or reload is triggered
    LaunchedEffect(platformEnum, reloadKey) {
        runningWebView?.loadUrl(platformUrl)
    }

    // Dynamic Focus Mode reactivity based on settings updates at runtime
    LaunchedEffect(focusModeSettings, runningWebView) {
        runningWebView?.let { webView ->
            if (focusModeSettings.isEnabled) {
                com.example.focus.FocusModeExecutor.getInstance().executeFocusMode(
                    webView = webView,
                    platform = platformEnum,
                    settings = focusModeSettings
                )
            }
        }
    }

    Box(modifier = modifier.background(DarkBackground)) {
        AndroidView(
            factory = { ctx ->
                webViewManager.createPlatformWebView(platformEnum).also { webview ->
                    webview.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    runningWebView = webview
                    webview.loadUrl(platformUrl)
                }
            },
            update = { webview ->
                // No dynamic update operations needed
            },
            modifier = Modifier.fillMaxSize().testTag("platform_real_webview_${platformId}")
        )
    }
}

// --- PlatformMockFeed & Composables ---

@Composable
private fun PlatformMockFeed(
    platformId: String,
    platformUrl: String,
    focusModeSettings: FocusModeSettings,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isFocusEnabled = focusModeSettings.isEnabled
    val platformEnum = when (platformId.lowercase()) {
        "ig", "instagram" -> Platform.INSTAGRAM
        "x", "twitter" -> Platform.X
        "yt", "youtube" -> Platform.YOUTUBE
        else -> Platform.INSTAGRAM
    }
    val activeFilters = focusModeSettings.platformFilters[platformEnum]?.enabledFilters ?: emptyMap()
    var instagramTab by remember { mutableIntStateOf(0) }
    var xSelectedTab by remember { mutableIntStateOf(0) }
    var youtubeSelectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Platform Badge Header card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, SubtleBorderColor, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            when (platformEnum) {
                                Platform.INSTAGRAM -> ColorInstagram.copy(alpha = 0.15f)
                                Platform.X -> Color.White.copy(alpha = 0.15f)
                                Platform.YOUTUBE -> ColorYoutube.copy(alpha = 0.15f)
                            },
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (platformEnum) {
                            Platform.INSTAGRAM -> Icons.Default.CameraAlt
                            Platform.X -> Icons.Default.Close
                            Platform.YOUTUBE -> Icons.Default.PlayArrow
                        },
                        contentDescription = null,
                        tint = when (platformEnum) {
                            Platform.INSTAGRAM -> ColorInstagram
                            Platform.X -> Color.White
                            Platform.YOUTUBE -> ColorYoutube
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = when (platformEnum) {
                            Platform.INSTAGRAM -> "Instagram Live"
                            Platform.X -> "X Platform Feed"
                            Platform.YOUTUBE -> "YouTube Central"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = platformUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            if (isFocusEnabled) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AccentIndigoDark.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentIndigoLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SHIELDED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                    }
                }
            }
        }

        // Segmented control to toggle Instagram feeds
        if (platformEnum == Platform.INSTAGRAM) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, SubtleBorderColor, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Explore Feed", "AI Intel Hub").forEachIndexed { index, title ->
                    val isSelected = instagramTab == index
                    val tabBgColor by animateColorAsState(
                        targetValue = if (isSelected) ColorInstagram else Color.Transparent,
                        animationSpec = tween(200),
                        label = "tabBg"
                    )
                    val tabTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else TextSecondary,
                        animationSpec = tween(200),
                        label = "tabText"
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabBgColor)
                            .clickable { instagramTab = index }
                            .padding(vertical = 10.dp)
                            .testTag("ig_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Layers else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = tabTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = tabTextColor
                            )
                        }
                    }
                }
            }
        }
                        
        // Segmented control to toggle X/Twitter feeds
        if (platformEnum == Platform.X) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, SubtleBorderColor, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Timeline", "AI Intel Hub").forEachIndexed { index, title ->
                    val isSelected = xSelectedTab == index
                    val tabBgColor by animateColorAsState(
                        targetValue = if (isSelected) AccentIndigo else Color.Transparent,
                        animationSpec = tween(200),
                        label = "tabBgX"
                    )
                    val tabTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else TextSecondary,
                        animationSpec = tween(200),
                        label = "tabTextX"
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabBgColor)
                            .clickable { xSelectedTab = index }
                            .padding(vertical = 10.dp)
                            .testTag("x_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Layers else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = tabTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = tabTextColor
                            )
                        }
                    }
                }
            }
        }

        // Segmented control to toggle YouTube feeds
        if (platformEnum == Platform.YOUTUBE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, SubtleBorderColor, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Recommendations", "AI Intel Hub").forEachIndexed { index, title ->
                    val isSelected = youtubeSelectedTab == index
                    val tabBgColor by animateColorAsState(
                        targetValue = if (isSelected) ColorYoutube else Color.Transparent,
                        animationSpec = tween(200),
                        label = "tabBgYT"
                    )
                    val tabTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else TextSecondary,
                        animationSpec = tween(200),
                        label = "tabTextYT"
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabBgColor)
                            .clickable { youtubeSelectedTab = index }
                            .padding(vertical = 10.dp)
                            .testTag("yt_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Layers else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = tabTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = tabTextColor
                            )
                        }
                    }
                }
            }
        }

        // Render platform feed stream
        when (platformEnum) {
            Platform.INSTAGRAM -> {
                if (instagramTab == 1) {
                    InstagramIntelView()
                } else {
                    // SUGGESTED ACCOUNTS HORIZONTAL SHELF
                    val hideSuggestedAccounts = isFocusEnabled && activeFilters[FilterType.HIDE_SUGGESTED_ACCOUNTS] == true
                    if (!hideSuggestedAccounts) {
                        InstagramSuggestedAccounts()
                    } else {
                        FocusFilterBanner(displayName = "Suggested Accounts")
                    }

                    // SUGGESTED POSTS
                    val hideSuggestedPosts = isFocusEnabled && activeFilters[FilterType.HIDE_SUGGESTED_POSTS] == true
                    
                    // Normal Feed card 1 (Always Visible)
                    InstagramPostCard(
                        authorName = "kotlin_weekly",
                        authorHandle = "Kotlin Weekly Digest",
                        caption = "🚀 Exploring the incredible power of Jetpack Compose in Android! Compose is standardizing dynamic Material 3 design systems worldwide. #AndroidDev #Kotlin #Compose",
                        postTopic = "Tech & Engineering",
                        gradColor = AccentIndigo
                    )

                    // REELS CARD
                    val hideReels = isFocusEnabled && activeFilters[FilterType.HIDE_REELS] == true
                    if (!hideReels) {
                        InstagramReelCard()
                    } else {
                        FocusFilterBanner(displayName = "Reels Content Stream")
                    }

                    // Normal Feed card 2
                    InstagramPostCard(
                        authorName = "google_ai",
                        authorHandle = "Google AI Studio",
                        caption = "✨ Built real-time application advisory with Gemini. Harness stateful ViewModels and secure repository flows seamlessly on Android. #GeminiAPI #AIEngineering",
                        postTopic = "Artificial Intelligence",
                        gradColor = AccentIndigoLight
                    )

                    // EXPLORE PROMPTS
                    val hideExplore = isFocusEnabled && activeFilters[FilterType.HIDE_EXPLORE] == true
                    if (!hideExplore) {
                        InstagramExploreCard()
                    } else {
                        FocusFilterBanner(displayName = "Algorithmic Explore Section")
                    }

                    if (!hideSuggestedPosts) {
                        InstagramPostCard(
                            authorName = "sponsored_tech",
                            authorHandle = "Suggested Sponsor",
                            caption = "🎁 Use SocialDash Premium to master distraction-free analytics and reclaim your valuable focused hours! Available globally. #Productivity #Focus",
                            postTopic = "Sponsored Recommendation",
                            gradColor = DarkSurfaceElevated
                        )
                    } else {
                        FocusFilterBanner(displayName = "Suggested Ads & Posts")
                    }
                }
            }

            Platform.X -> {
                if (xSelectedTab == 1) {
                    XIntelView()
                } else {
                    // TABS FOR YOU / FOLLOWING
                    val hideForYou = isFocusEnabled && activeFilters[FilterType.HIDE_FOR_YOU] == true
                    XTimelineTabs(isForYouHidden = hideForYou)

                    // Normal regular tweet
                    XTweetCard(
                        author = "Kotlin Language",
                        username = "kotlin",
                        text = "The new Kotlin compiler optimizes compilation speeds up to 2x. Learn how to migrate your multi-module configuration.",
                        timestamp = "2h",
                        likes = "1,245",
                        reposts = "312"
                    )

                    // SUGGESTED USERS
                    val hideSuggestedUsers = isFocusEnabled && activeFilters[FilterType.HIDE_SUGGESTED_USERS] == true
                    if (!hideSuggestedUsers) {
                        XSuggestedUsers()
                    } else {
                        FocusFilterBanner(displayName = "Suggested Users (Who to Follow)")
                    }

                    // TWEET 2 (Algorithmic / Viral Tweet - only shown if For You is not hidden)
                    if (!hideForYou) {
                        XTweetCard(
                            author = "Algorithmic Thread Maker",
                            username = "deep_scrolls_algorithm",
                            text = "🧵 Here are 57 productivity secrets that elite developers use to code for 14 hours straight without moving dynamic muscles. Let's dive in! 👇",
                            timestamp = "Trending now",
                            likes = "14.2K",
                            reposts = "8.3K"
                        )
                    } else {
                        FocusFilterBanner(displayName = "Algorithmic 'For You' Stream")
                    }

                    // TRENDING LIST
                    val hideTrending = isFocusEnabled && activeFilters[FilterType.HIDE_TRENDING] == true
                    if (!hideTrending) {
                        XTrendingPanel()
                    } else {
                        FocusFilterBanner(displayName = "Trending Hashtags & Topics")
                    }

                    // Normal regular tweet 3
                    XTweetCard(
                        author = "Jetpack Compose",
                        username = "jetpack_compose",
                        text = "Dynamic insets, layout class adaptive designs, and custom transitions are easier than ever with Compose 2024. Let's make Android beautiful!",
                        timestamp = "5h",
                        likes = "840",
                        reposts = "192"
                    )
                }
            }

            Platform.YOUTUBE -> {
                if (youtubeSelectedTab == 1) {
                    YouTubeIntelView()
                } else {
                    // SUGGESTED SEARCH BOX
                    YouTubeSearchBox()

                    // HOME RECOMMENDATIONS
                    val hideHomeRecs = isFocusEnabled && activeFilters[FilterType.HIDE_HOME_RECOMMENDATIONS] == true
                    if (!hideHomeRecs) {
                        YouTubeVideoCard(
                            title = "Android Clean Architecture in 2026: The Master Walkthrough",
                            channel = "Developer Wisdom",
                            views = "150K views",
                            posted = "3 days ago",
                            duration = "24:15",
                            color = AccentIndigo
                        )

                        YouTubeVideoCard(
                            title = "Building custom Jetpack Compose synthesizers & waveforms",
                            channel = "Audio Craft",
                            views = "45K views",
                            posted = "1 week ago",
                            duration = "18:40",
                            color = AccentIndigoLight
                        )
                    } else {
                        YouTubeFocusSerenityPrompt()
                    }

                    // SHORTS SHELF
                    val hideShorts = isFocusEnabled && activeFilters[FilterType.HIDE_SHORTS] == true
                    if (!hideShorts) {
                        YouTubeShortsShelf()
                    } else {
                        FocusFilterBanner(displayName = "YouTube Shorts")
                    }

                    // SUGGESTED VIDEOS (Secondary Recommendations)
                    val hideSuggestedVideos = isFocusEnabled && activeFilters[FilterType.HIDE_SUGGESTED_VIDEOS] == true
                    if (!hideSuggestedVideos) {
                        YouTubeSuggestedVideosList()
                    } else {
                        FocusFilterBanner(displayName = "Suggested Sidebar Videos")
                    }

                    // END SCREEN OVERLAYS
                    val hideEndScreen = isFocusEnabled && activeFilters[FilterType.HIDE_END_SCREEN_RECOMMENDATIONS] == true
                    if (hideHomeRecs && hideSuggestedVideos && hideEndScreen) {
                        // Fully clean YouTube view
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AccentIndigoLight, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Serene Mode Activated",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "All algorithmic triggers and suggestive grids have been cleanly intercepted.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusFilterBanner(displayName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SubtleBorderColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "$displayName Blocked",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = "Shielded by active Focus Mode parameters.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun InstagramSuggestedAccounts() {
    Column {
        Text(
            text = "Suggested Creator Circles",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val suggestions = listOf("compiler_dev", "compose_wizard", "g_android", "kotlin_guru")
            suggestions.forEach { name ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SubtleBorderColor),
                    modifier = Modifier.width(110.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Brush.linearGradient(listOf(ColorInstagram, AccentIndigo)),
                                    RoundedCornerShape(22.dp)
                                )
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DarkSurface, RoundedCornerShape(20.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Follow",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentIndigoLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstagramPostCard(
    authorName: String,
    authorHandle: String,
    caption: String,
    postTopic: String,
    gradColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(gradColor, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = authorName.take(1).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = authorName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = authorHandle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(gradColor.copy(alpha = 0.2f), DarkSurfaceElevated)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "COMMUNITY PICKS: $postTopic",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentIndigoLight
                    )
                }
            }

            var isLiked by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) ColorInstagram else TextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { isLiked = !isLiked }
                    )
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Share",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Icon(Icons.Default.Star, contentDescription = "Save", tint = TextPrimary, modifier = Modifier.size(24.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun InstagramReelCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = ColorInstagram, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reels Stream Recommend", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Text("LIVE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ColorInstagram)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Brush.linearGradient(listOf(ColorInstagram.copy(alpha = 0.3f), AccentIndigoDark)), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Reel", tint = Color.White, modifier = Modifier.size(44.dp))
            }
        }
    }
}

@Composable
private fun InstagramExploreCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Trending in Explore", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("#MountainAdventures", "#JetpackComposing", "#Kotlin2").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(tag, style = MaterialTheme.typography.labelSmall, color = AccentIndigoLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun XTimelineTabs(isForYouHidden: Boolean) {
    var selectedTab by remember { mutableIntStateOf(if (isForYouHidden) 1 else 0) }
    
    LaunchedEffect(isForYouHidden) {
        if (isForYouHidden) {
            selectedTab = 1
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (!isForYouHidden) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = 0 }
                    .background(if (selectedTab == 0) DarkSurfaceElevated else Color.Transparent, RoundedCornerShape(6.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "For You",
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 0) TextPrimary else TextSecondary
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { selectedTab = 1 }
                .background(if (selectedTab == 1) DarkSurfaceElevated else Color.Transparent, RoundedCornerShape(6.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Following Only",
                fontWeight = FontWeight.Bold,
                color = if (selectedTab == 1) TextPrimary else TextSecondary
            )
        }
    }
}

@Composable
private fun XTweetCard(
    author: String,
    username: String,
    text: String,
    timestamp: String,
    likes: String,
    reposts: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(AccentIndigo, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(author.take(1), color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(author, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("@$username • $timestamp", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Comment, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text("34", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(reposts, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(likes, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun XSuggestedUsers() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SubtleBorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Who to follow", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            listOf("composable_ninja", "android_dev_group").forEach { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).background(AccentIndigo, RoundedCornerShape(12.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(user, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("@$user", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text("Follow", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun XTrendingPanel() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SubtleBorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Trends for you", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            listOf(
                Triple("Trending in Technology", "#Kotlin2", "25.4K posts"),
                Triple("Trending in Android", "#ComposeMaterial3", "12.8K posts"),
                Triple("Trending globally", "Artificial Intelligence", "145K posts")
            ).forEach { (category, topic, count) ->
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(category, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text(topic, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(count, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun YouTubeSearchBox() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Search subscription feeds...", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun YouTubeVideoCard(
    title: String,
    channel: String,
    views: String,
    posted: String,
    duration: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Brush.linearGradient(listOf(color.copy(alpha = 0.2f), DarkSurfaceElevated))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Tutorial", tint = Color.White, modifier = Modifier.size(48.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(duration, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Row(modifier = Modifier.padding(12.dp)) {
                Box(modifier = Modifier.size(36.dp).background(color, RoundedCornerShape(18.dp)))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$channel • $views • $posted", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun YouTubeShortsShelf() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = ColorYoutube, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("YouTube Shorts", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                "Designing Custom Sliders" to "2.1M views",
                "How I stay highly focused in 2026" to "820K views",
                "Unlocking Moshi serialization" to "140K views"
            ).forEach { (title, count) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(140.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Brush.linearGradient(listOf(ColorYoutube.copy(alpha = 0.15f), DarkSurfaceElevated)))
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(count, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YouTubeSuggestedVideosList() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Suggested Videos (Up Next)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            listOf(
                Triple("Retrofit in Multi-Module Projects", "Modern Android", "10M views"),
                Triple("Build interactive charts with Vico", "Design Patterns", "45K views")
            ).forEach { (title, author, views) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.size(60.dp, 40.dp).background(AccentIndigo, RoundedCornerShape(4.dp)))
                    Column {
                        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("$author • $views", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun YouTubeFocusSerenityPrompt() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SubtleBorderColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = AccentIndigoLight, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Algorithmic Recommendations Filtered",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "The Home Feed stream is shielded to prevent mindless loops. Search directly above to locate subscriptions or specific educational material.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun InstagramIntelView() {
    val intelData = remember { MockData.generateMockPlatformIntel(Platform.INSTAGRAM) }
    
    // Overall Stats Header
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ig_intel_header_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ColorInstagram.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ColorInstagram,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "Instagram AI Insight Index",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Green.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.Green)
                        )
                        Text(
                            text = "LIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OVERALL SENTIMENT",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intelData.overallSentiment,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentIndigoLight
                    )
                }
                
                Box(modifier = Modifier.width(1.dp).height(38.dp).background(SubtleBorderColor.copy(alpha = 0.5f)))
                
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(
                        text = "VOLUME TREND",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intelData.volumeTrend,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorInstagram
                    )
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Topic list section header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = ColorInstagram,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Trending Intel Clusters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = "${intelData.trendingTopics.size} Topics detected",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
    
    // List of Trending topics renders here
    intelData.trendingTopics.forEach { topic ->
        InstagramTrendingTopicCard(topic = topic)
    }
}

@Composable
private fun InstagramTrendingTopicCard(topic: TrendingTopic) {
    var isExpanded by remember { mutableStateOf(false) }
    var isDetailDialogOpen by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .testTag("ig_topic_card_${topic.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, if (isExpanded) ColorInstagram.copy(alpha = 0.5f) else SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Topic name, impressions and arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorInstagram.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = topic.intelCategory.name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorInstagram
                            )
                        }
                        
                        Text(
                            text = "Velocity: ${topic.velocity}x",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (topic.title.startsWith("#") || topic.title.contains("_")) topic.title else "#${topic.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format("%,d", topic.impressions),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Impressions",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                    
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Expanded Section
            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))
                
                // 1. Topic AI Summary Block
                topic.topicSummary?.let { summary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground.copy(alpha = 0.5f))
                            .border(1.dp, SubtleBorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ColorInstagram,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AI INTEL SUMMARY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary.mainConcept,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = summary.summaryText,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                        
                        if (summary.keyBulletPoints.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            summary.keyBulletPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        color = ColorInstagram,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = point,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // 2. Why Trending Block
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "WHY IT IS TRENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = topic.whyTrending,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // 3. Mentioned Accounts
                if (topic.mentionedAccounts.isNotEmpty()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = AccentIndigoLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "KEY MENTIONING CREATORS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            topic.mentionedAccounts.forEach { account ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DarkBackground.copy(alpha = 0.5f))
                                        .border(0.5.dp, SubtleBorderColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val seedColor = when (account.avatarColorSeed.lowercase()) {
                                        "blue" -> Color(0xFF3B82F6)
                                        "silver" -> Color(0xFF94A3B8)
                                        "amber" -> Color(0xFFF59E0B)
                                        "pink" -> Color(0xFFEC4899)
                                        "indigo" -> Color(0xFF6366F1)
                                        "orange" -> Color(0xFFF97316)
                                        "red" -> Color(0xFFEF4444)
                                        else -> ColorInstagram
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(seedColor)
                                    )
                                    
                                    Column {
                                        Text(
                                            text = account.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${account.handle} • ${account.followerCount}",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
                
                // 4. Action Button for Detailed Insights
                Button(
                    onClick = { isDetailDialogOpen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("ig_btn_analyze_topic_${topic.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorInstagram.copy(alpha = 0.15f),
                        contentColor = ColorInstagram
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "View Technical Deep-Dive",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Topic Detail Dialog
    if (isDetailDialogOpen) {
        InstagramTopicDetailDialog(
            topic = topic,
            onDismissRequest = { isDetailDialogOpen = false }
        )
    }
}

@Composable
private fun InstagramTopicDetailDialog(
    topic: TrendingTopic,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = ColorInstagram),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_dismiss_ig_dialog")
            ) {
                Text("Complete Analysis", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ColorInstagram.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ColorInstagram,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Text(
                    text = "Aesthetic Intel: ${topic.title}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Category bubble & stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .border(0.5.dp, SubtleBorderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = topic.intelCategory.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${topic.velocity}x Growth Rate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorInstagram
                        )
                        Text(
                            text = "${String.format("%,d", topic.impressions)} Audience impressions",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
                
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.3f))
                
                // Why Trending Detail
                Column {
                    Text(
                        text = "CRITICAL VIRAL TRIGGER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorInstagram,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.whyTrending,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
                
                // Summary Detail Keypoints
                topic.topicSummary?.let { summary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .border(1.dp, SubtleBorderColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "AI DECODED PATTERN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = summary.summaryText,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (summary.keyBulletPoints.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            summary.keyBulletPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Green,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = point,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Content Examples Grid (Visual Highlight)
                if (topic.contentExamples.isNotEmpty()) {
                    Column {
                        Text(
                            text = "MONITORED SOCIAL POST EVIDENCE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        topic.contentExamples.forEach { postContent ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                border = BorderStroke(0.5.dp, SubtleBorderColor.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
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
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.linearGradient(
                                                            listOf(ColorInstagram, AccentIndigoLight)
                                                        )
                                                    )
                                            )
                                            Text(
                                                text = "Monitored Creator",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = "Active Reel",
                                            fontSize = 8.sp,
                                            color = ColorInstagram,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = postContent,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("ig_topic_dialog_${topic.id}")
    )
}

@Composable
private fun XIntelView() {
    val intelData = remember { MockData.generateMockPlatformIntel(Platform.X) }
    
    // Overall Stats Header
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("x_intel_header_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "X Real-Time Intel Tracker",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentIndigo.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AccentIndigo)
                        )
                        Text(
                            text = "LIVE FEED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OVERALL SENTIMENT",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intelData.overallSentiment,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Box(modifier = Modifier.width(1.dp).height(38.dp).background(SubtleBorderColor.copy(alpha = 0.5f)))
                
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(
                        text = "VOLUME TREND",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intelData.volumeTrend,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentIndigoLight
                    )
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Topic list section header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = AccentIndigoLight,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Trending Discussion Clusters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = "${intelData.trendingTopics.size} Topics detected",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
    
    // List of Trending topics renders here
    intelData.trendingTopics.forEach { topic ->
        XTrendingTopicCard(topic = topic)
    }
}

@Composable
private fun XTrendingTopicCard(topic: TrendingTopic) {
    var isExpanded by remember { mutableStateOf(false) }
    var isDetailDialogOpen by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .testTag("x_topic_card_${topic.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, if (isExpanded) Color.White.copy(alpha = 0.4f) else SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Topic name, impressions and arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = topic.intelCategory.name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        Text(
                            text = "Velocity: ${topic.velocity}x",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (topic.title.startsWith("#") || topic.title.contains("_")) topic.title else "#${topic.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format("%,d", topic.impressions),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Impressions",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                    
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Expanded Section
            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))
                
                // 1. Topic AI Summary Block
                topic.topicSummary?.let { summary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground.copy(alpha = 0.5f))
                            .border(1.dp, SubtleBorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AccentIndigoLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AI INTEL DECODED SUMMARY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary.mainConcept,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = summary.summaryText,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                        
                        if (summary.keyBulletPoints.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            summary.keyBulletPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        color = AccentIndigoLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = point,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // 2. Why Trending Block
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "WHY IT IS TRENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = topic.whyTrending,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // 3. Mentioned Accounts
                if (topic.mentionedAccounts.isNotEmpty()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = AccentIndigoLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "ACCOUNTS DISCUSSING / KEY INFLUENCERS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            topic.mentionedAccounts.forEach { account ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DarkBackground.copy(alpha = 0.5f))
                                        .border(0.5.dp, SubtleBorderColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val seedColor = when (account.avatarColorSeed.lowercase()) {
                                        "blue" -> Color(0xFF3B82F6)
                                        "silver" -> Color(0xFF94A3B8)
                                        "amber" -> Color(0xFFF59E0B)
                                        "pink" -> Color(0xFFEC4899)
                                        "indigo" -> Color(0xFF6366F1)
                                        "orange" -> Color(0xFFF97316)
                                        "red" -> Color(0xFFEF4444)
                                        else -> AccentIndigoLight
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(seedColor)
                                    )
                                    
                                    Column {
                                        Text(
                                            text = account.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${account.handle} • ${account.followerCount}",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
                
                // 4. Action Button for Detailed Insights
                Button(
                    onClick = { isDetailDialogOpen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("x_btn_analyze_topic_${topic.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "View Sentiment Deep-Dive",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Topic Detail Dialog
    if (isDetailDialogOpen) {
        XTopicDetailDialog(
            topic = topic,
            onDismissRequest = { isDetailDialogOpen = false }
        )
    }
}

@Composable
private fun XTopicDetailDialog(
    topic: TrendingTopic,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_dismiss_x_dialog")
            ) {
                Text("Complete Analysis", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Text(
                    text = "Discussion Analysis: ${topic.title}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Category bubble & stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .border(0.5.dp, SubtleBorderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = topic.intelCategory.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${topic.velocity}x Speed Multiplier",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                        Text(
                            text = "${String.format("%,d", topic.impressions)} Total Impressions",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
                
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.3f))
                
                // Why Trending Detail
                Column {
                    Text(
                        text = "TRIGGER MOMENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.whyTrending,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
                
                // Summary Detail Keypoints
                topic.topicSummary?.let { summary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .border(1.dp, SubtleBorderColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "AI-DECODED VIEWPOINT CORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = summary.summaryText,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (summary.keyBulletPoints.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            summary.keyBulletPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AccentIndigoLight,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = point,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Content Examples (Monitored Tweets)
                if (topic.contentExamples.isNotEmpty()) {
                    Column {
                        Text(
                            text = "EVIDENCE TWEETS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        topic.contentExamples.forEach { postContent ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                border = BorderStroke(0.5.dp, SubtleBorderColor.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
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
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.15f))
                                            )
                                            Text(
                                                text = "Verified Account",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = "X Live Tweet",
                                            fontSize = 8.sp,
                                            color = AccentIndigoLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = postContent,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("x_topic_dialog_${topic.id}")
    )
}

@Composable
private fun YouTubeIntelView() {
    val intelData = remember { MockData.generateMockPlatformIntel(Platform.YOUTUBE) }
    
    // Overall Stats Header
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("yt_intel_header_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ColorYoutube.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = ColorYoutube,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "YouTube Creator Intel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ColorYoutube.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ColorYoutube)
                        )
                        Text(
                            text = "LIVE PULSE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorYoutube
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CREATOR SENTIMENT",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intelData.overallSentiment,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Box(modifier = Modifier.width(1.dp).height(38.dp).background(SubtleBorderColor.copy(alpha = 0.5f)))
                
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(
                        text = "TREND VELOCITY",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intelData.volumeTrend,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorYoutube
                    )
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Topic list section header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = ColorYoutube,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Trending Creator Topics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = "${intelData.trendingTopics.size} Clusters detected",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
    
    // List of Trending topics renders here
    intelData.trendingTopics.forEach { topic ->
        YouTubeTrendingTopicCard(topic = topic)
    }
}

@Composable
private fun YouTubeTrendingTopicCard(topic: TrendingTopic) {
    var isExpanded by remember { mutableStateOf(false) }
    var isDetailDialogOpen by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .testTag("yt_topic_card_${topic.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, if (isExpanded) ColorYoutube.copy(alpha = 0.5f) else SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Topic name, impressions and arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorYoutube.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = topic.intelCategory.name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorYoutube
                            )
                        }
                        
                        Text(
                            text = "Velocity: ${topic.velocity}x",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorYoutube
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format("%,d", topic.impressions),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Views",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                    
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Expanded Section
            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))
                
                // 1. Topic AI Summary Block
                topic.topicSummary?.let { summary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground.copy(alpha = 0.5f))
                            .border(1.dp, SubtleBorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ColorYoutube,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AI INTEL SYSTEM SUMMARY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary.mainConcept,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = summary.summaryText,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                        
                        if (summary.keyBulletPoints.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            summary.keyBulletPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        color = ColorYoutube,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = point,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // 2. Why Trending Block
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "WHY IT IS TRENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = topic.whyTrending,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // 3. Mentioned Channels
                if (topic.mentionedAccounts.isNotEmpty()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = ColorYoutube,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "CHANNELS DISCUSSING TOPICS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            topic.mentionedAccounts.forEach { account ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DarkBackground.copy(alpha = 0.5f))
                                        .border(0.5.dp, SubtleBorderColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val seedColor = when (account.avatarColorSeed.lowercase()) {
                                        "red" -> Color(0xFFEF4444)
                                        "gray" -> Color(0xFF6B7280)
                                        "dark_gray" -> Color(0xFF374151)
                                        "android_green" -> Color(0xFF3DDC84)
                                        else -> ColorYoutube
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(seedColor)
                                    )
                                    
                                    Column {
                                        Text(
                                            text = account.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${account.handle} • ${account.followerCount}",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
                
                // 4. Action Button for Detailed Insights
                Button(
                    onClick = { isDetailDialogOpen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("yt_btn_analyze_topic_${topic.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "View Creator Deep-Dive",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Topic Detail Dialog
    if (isDetailDialogOpen) {
        YouTubeTopicDetailDialog(
            topic = topic,
            onDismissRequest = { isDetailDialogOpen = false }
        )
    }
}

@Composable
private fun YouTubeTopicDetailDialog(
    topic: TrendingTopic,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = ColorYoutube),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_dismiss_yt_dialog")
            ) {
                Text("Complete Analysis", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ColorYoutube.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = ColorYoutube,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Text(
                    text = "Topic Analysis: ${topic.title}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Category bubble & stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .border(0.5.dp, SubtleBorderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = topic.intelCategory.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${topic.velocity}x Speed Multiplier",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorYoutube
                        )
                        Text(
                            text = "${String.format("%,d", topic.impressions)} Views",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
                
                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.3f))
                
                // Why Trending Detail
                Column {
                    Text(
                        text = "TRIGGER MOMENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.whyTrending,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
                
                // Summary Detail Keypoints
                topic.topicSummary?.let { summary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .border(1.dp, SubtleBorderColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "AI-DECODED VIEWPOINT CORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = summary.summaryText,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (summary.keyBulletPoints.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            summary.keyBulletPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ColorYoutube,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = point,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Content Examples (Monitored Videos)
                if (topic.contentExamples.isNotEmpty()) {
                    Column {
                        Text(
                            text = "EVIDENCE VIDEOS / SAMPLES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        topic.contentExamples.forEach { postContent ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                border = BorderStroke(0.5.dp, SubtleBorderColor.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
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
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(ColorYoutube.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = ColorYoutube,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                            Text(
                                                text = "Monitored Video",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = "YT Live Stream / Clip",
                                            fontSize = 8.sp,
                                            color = ColorYoutube,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = postContent,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("yt_topic_dialog_${topic.id}")
    )
}

