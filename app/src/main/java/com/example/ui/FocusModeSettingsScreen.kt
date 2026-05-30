package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.FilterType
import com.example.model.FocusModeSettings
import com.example.model.Platform
import com.example.ui.theme.*
import com.example.viewmodel.FocusModeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FocusModeSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: FocusModeViewModel = viewModel()
) {
    val focusModeSettingsState by viewModel.focusModeSettings.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Local mutable state copies of the settings, synced which we can modify and then persist with visual save
    var isEnabled by remember { mutableStateOf(false) }
    var localFilters by remember { mutableStateOf<Map<Platform, Map<FilterType, Boolean>>>(emptyMap()) }

    // Sync from source settings on change
    LaunchedEffect(focusModeSettingsState) {
        isEnabled = focusModeSettingsState.isEnabled
        localFilters = focusModeSettingsState.platformFilters.mapValues { (_, platformFilters) ->
            platformFilters.enabledFilters
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Mode Filters", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_focus_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Master Toggle Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("card_focus_mode_master"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Focus Shield",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentIndigoLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Toggle main Focus Shield to apply active filters across all social feeds.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { isEnabled = it },
                                modifier = Modifier.testTag("switch_focus_mode_master_toggle"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentIndigo,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }
                    }
                }
            }

            // Platform Filter groups (Instagram, X Filters, YouTube)
            Platform.values().forEach { platform ->
                item {
                    val platformName = when (platform) {
                        Platform.INSTAGRAM -> "Instagram Filters"
                        Platform.X -> "X Filters"
                        Platform.YOUTUBE -> "YouTube Filters"
                    }
                    val platformIcon = when (platform) {
                        Platform.INSTAGRAM -> Icons.Default.CameraAlt
                        Platform.X -> Icons.Default.Close
                        Platform.YOUTUBE -> Icons.Default.PlayArrow
                    }
                    val platformColor = when (platform) {
                        Platform.INSTAGRAM -> ColorInstagram
                        Platform.X -> Color.White
                        Platform.YOUTUBE -> ColorYoutube
                    }
                    val testTagPrefix = when (platform) {
                        Platform.INSTAGRAM -> "card_instagram_filters"
                        Platform.X -> "card_x_filters"
                        Platform.YOUTUBE -> "card_youtube_filters"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag(testTagPrefix),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, SubtleBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = platformIcon,
                                    contentDescription = "$platformName Icon",
                                    tint = platformColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = platformName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            
                            HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.4f), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // List relevant options
                            val filtersForPlatform = FilterType.values().filter { it.platform == platform }
                            filtersForPlatform.forEachIndexed { index, filter ->
                                val isActive = localFilters[platform]?.get(filter) ?: false
                                val filterTestTag = when (filter) {
                                    FilterType.HIDE_REELS -> "switch_filter_hide_reels"
                                    FilterType.HIDE_EXPLORE -> "switch_filter_hide_explore"
                                    FilterType.HIDE_SUGGESTED_POSTS -> "switch_filter_hide_suggested_posts"
                                    FilterType.HIDE_SUGGESTED_ACCOUNTS -> "switch_filter_hide_suggested_accounts"
                                    FilterType.HIDE_FOR_YOU -> "switch_filter_hide_for_you"
                                    FilterType.HIDE_TRENDING -> "switch_filter_hide_trending"
                                    FilterType.HIDE_SUGGESTED_USERS -> "switch_filter_hide_suggested_users"
                                    FilterType.HIDE_SHORTS -> "switch_filter_hide_shorts"
                                    FilterType.HIDE_HOME_RECOMMENDATIONS -> "switch_filter_hide_home_recs"
                                    FilterType.HIDE_SUGGESTED_VIDEOS -> "switch_filter_hide_suggested_videos"
                                    FilterType.HIDE_END_SCREEN_RECOMMENDATIONS -> "switch_filter_hide_end_screen_recs"
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = filter.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isEnabled) TextPrimary else TextMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Switch(
                                        checked = isActive,
                                        enabled = isEnabled,
                                        onCheckedChange = { active ->
                                            val updatedPlatformMap = localFilters[platform]?.toMutableMap() ?: mutableMapOf()
                                            updatedPlatformMap[filter] = active
                                            localFilters = localFilters + (platform to updatedPlatformMap)
                                        },
                                        modifier = Modifier.testTag(filterTestTag),
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = AccentIndigo,
                                            uncheckedThumbColor = TextMuted,
                                            uncheckedTrackColor = DarkSurfaceElevated
                                        )
                                    )
                                }

                                if (index < filtersForPlatform.size - 1) {
                                    HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.2f), thickness = 1.dp)
                                }
                            }
                        }
                    }
                }
            }

            // Save Buttons
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val platformMap = localFilters.mapValues { (p, map) ->
                            com.example.model.PlatformFilters(p, map)
                        }
                        val settings = FocusModeSettings(isEnabled = isEnabled, platformFilters = platformMap)
                        viewModel.saveSettings(settings)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Focus mode settings saved!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_focus_mode"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Icon", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Configurations", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // Reset defaults button
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.resetSettings()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Reset back to default settings!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_reset_focus_mode_defaults"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SubtleBorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentIndigoLight)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Icon", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset to Defaults", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
