package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.viewmodel.SocialDashViewModel
import com.example.viewmodel.FocusModeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onFocusModeSettingsClick: () -> Unit = {},
    onRelatedContentClick: () -> Unit = {},
    viewModel: SocialDashViewModel = viewModel()
) {
    // Collect State
    val userSettings by viewModel.settings.collectAsState()
    
    // Focus Mode State & ViewModel
    val focusModeViewModel: FocusModeViewModel = viewModel()
    val focusModeSettings by focusModeViewModel.focusModeSettings.collectAsState()
    
    // Local State mapped to UI
    var aiDigestsEnabled by remember { mutableStateOf(userSettings.aiDigestsEnabled) }
    var notificationStyle by remember { mutableStateOf(userSettings.notificationStyle.name) }
    var syncFreq by remember { mutableIntStateOf(userSettings.syncFrequencyMinutes) }
    var interestTopics by remember { mutableStateOf(userSettings.interestTopics) }
    var activityFilters by remember { mutableStateOf(userSettings.activityFilters) }
    var morningBriefingTime by remember { mutableStateOf(userSettings.morningBriefingTime) }

    // Dialog trigger states
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var newTopicText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userSettings) {
        aiDigestsEnabled = userSettings.aiDigestsEnabled
        notificationStyle = userSettings.notificationStyle.name
        syncFreq = userSettings.syncFrequencyMinutes
        interestTopics = userSettings.interestTopics
        activityFilters = userSettings.activityFilters
        morningBriefingTime = userSettings.morningBriefingTime
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            // 1. AI DIGESTS SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AI DIGESTS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        com.example.model.Platform.values().forEachIndexed { index, platform ->
                            val isChecked = aiDigestsEnabled[platform] ?: false
                            val logoColor = when(platform) {
                                com.example.model.Platform.INSTAGRAM -> ColorInstagram
                                com.example.model.Platform.X -> Color.White
                                com.example.model.Platform.YOUTUBE -> ColorYoutube
                            }
                            val logoIcon = when(platform) {
                                com.example.model.Platform.INSTAGRAM -> Icons.Default.CameraAlt
                                com.example.model.Platform.X -> Icons.Default.AlternateEmail
                                com.example.model.Platform.YOUTUBE -> Icons.Default.PlayArrow
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = logoIcon,
                                    contentDescription = null,
                                    tint = logoColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = when (platform) {
                                        com.example.model.Platform.INSTAGRAM -> "Instagram"
                                        com.example.model.Platform.X -> "X"
                                        com.example.model.Platform.YOUTUBE -> "YouTube"
                                    },
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        aiDigestsEnabled = aiDigestsEnabled + (platform to it)
                                    },
                                    modifier = Modifier.testTag("switch_${platform.name.lowercase()}"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AccentIndigo,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = DarkSurfaceElevated
                                    )
                                )
                            }
                            if (index < com.example.model.Platform.values().size - 1) {
                                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f), thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            // 2. NOTIFICATION STYLE SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "NOTIFICATION STYLE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val styles = listOf(
                            com.example.model.NotificationStyle.POPUP to "Popup",
                            com.example.model.NotificationStyle.IN_APP to "In-App",
                            com.example.model.NotificationStyle.BOTH to "Both",
                            com.example.model.NotificationStyle.ON_OPEN to "On Open"
                        )
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            styles.forEach { (style, label) ->
                                val isSelected = notificationStyle == style.name
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { notificationStyle = style.name },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("notif_style_${style.name.lowercase()}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentIndigo,
                                        selectedLabelColor = Color.White,
                                        containerColor = DarkSurfaceElevated,
                                        labelColor = TextPrimary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = SubtleBorderColor,
                                        selectedBorderColor = AccentIndigoLight
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. SYNC FREQUENCY SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SYNC FREQUENCY",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val frequencies = listOf(
                            30 to "30 Minutes",
                            60 to "1 Hour",
                            120 to "2 Hours",
                            0 to "Manual"
                        )
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            frequencies.forEach { (minutes, label) ->
                                val isSelected = syncFreq == minutes
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { syncFreq = minutes },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("sync_freq_${minutes}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentIndigo,
                                        selectedLabelColor = Color.White,
                                        containerColor = DarkSurfaceElevated,
                                        labelColor = TextPrimary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = SubtleBorderColor,
                                        selectedBorderColor = AccentIndigoLight
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. MORNING BRIEFING TIME PICKER SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePickerDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MORNING BRIEFING",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentIndigoLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Set your dynamic summary digest delivery time",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            modifier = Modifier.testTag("edit_morning_briefing_time_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = AccentIndigoLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val parts = morningBriefingTime.split(":")
                                val hour = parts.firstOrNull()?.toIntOrNull() ?: 8
                                val min = parts.lastOrNull()?.toIntOrNull() ?: 0
                                val formattedTime = String.format("%02d:%02d", hour, min)
                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 5. INTEREST TOPICS SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "INTEREST TOPICS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Topics which customize feed priorities and summaries",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            interestTopics.forEach { topic ->
                                InputChip(
                                    selected = true,
                                    onClick = { interestTopics = interestTopics - topic },
                                    label = { Text(topic) },
                                    trailingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove $topic",
                                            modifier = Modifier.size(14.dp)
                                        ) 
                                    },
                                    colors = InputChipDefaults.inputChipColors(
                                        containerColor = DarkSurfaceElevated,
                                        labelColor = TextPrimary,
                                        trailingIconColor = TextSecondary
                                    ),
                                    border = InputChipDefaults.inputChipBorder(
                                        enabled = true,
                                        selected = true,
                                        borderColor = SubtleBorderColor
                                    )
                                )
                            }
                            
                            InputChip(
                                selected = false,
                                onClick = { showAddTopicDialog = true },
                                label = { Text("Add Tag") },
                                leadingIcon = { 
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Topic",
                                        modifier = Modifier.size(14.dp),
                                        tint = AccentIndigoLight
                                    ) 
                				},
                                modifier = Modifier.testTag("add_interest_topic_chip"),
                                colors = InputChipDefaults.inputChipColors(
                                    containerColor = AccentIndigoDark,
                                    labelColor = AccentIndigoLight
                                ),
                                border = InputChipDefaults.inputChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = AccentIndigoLight.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }

            // 6. ACTIVITY FILTERS SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ACTIVITY FILTERS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toggle digital interactions included in reports",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.example.model.ActivityFilter.values().forEach { filter ->
                                val isSelected = activityFilters.contains(filter)
                                val label = when (filter) {
                                    com.example.model.ActivityFilter.POSTS -> "Posts"
                                    com.example.model.ActivityFilter.LIKES -> "Likes"
                                    com.example.model.ActivityFilter.COMMENTS -> "Comments"
                                    com.example.model.ActivityFilter.REPOSTS -> "Reposts"
                                }
                                
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        activityFilters = if (isSelected) {
                                            activityFilters - filter
                                        } else {
                                            activityFilters + filter
                                        }
                                    },
                                    label = { Text(label) },
                                    leadingIcon = if (isSelected) {
                                        { 
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            ) 
                                        }
                                    } else null,
                                    modifier = Modifier.testTag("activity_filter_${filter.name.lowercase()}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentIndigo,
                                        selectedLabelColor = Color.White,
                                        containerColor = DarkSurfaceElevated,
                                        labelColor = TextPrimary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = SubtleBorderColor,
                                        selectedBorderColor = AccentIndigoLight
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 7. FOCUS MODE SECTION
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("focus_mode_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "FOCUS SHIELD MODE",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentIndigoLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Activate custom distraction-free feed blocking parameters.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = focusModeSettings.isEnabled,
                                onCheckedChange = { focusModeViewModel.toggleFocusMode(it) },
                                modifier = Modifier.testTag("switch_focus_mode_master"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentIndigo,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onFocusModeSettingsClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_configure_focus_filters"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigoDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = AccentIndigoLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Configure Focus Filters",
                                        color = AccentIndigoLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = AccentIndigoLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRelatedContentClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_configure_related_content"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigoDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AccentIndigoLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Related Content Engine",
                                        color = AccentIndigoLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = AccentIndigoLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (focusModeSettings.isEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.5f), thickness = 1.dp)
                            
                            com.example.model.Platform.values().forEach { platform ->
                                val branding = when (platform) {
                                    com.example.model.Platform.INSTAGRAM -> Triple("Instagram", ColorInstagram, Icons.Default.CameraAlt)
                                    com.example.model.Platform.X -> Triple("X Platform", Color.White, Icons.Default.Close)
                                    com.example.model.Platform.YOUTUBE -> Triple("YouTube", ColorYoutube, Icons.Default.PlayArrow)
                                }
                                val pFilters = focusModeSettings.platformFilters[platform]

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = branding.third,
                                        contentDescription = branding.first,
                                        tint = branding.second,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = branding.first.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted,
                                        letterSpacing = 1.sp
                                    )
                                }

                                pFilters?.enabledFilters?.forEach { (filterType, isFilterActive) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                                    ) {
                                        Text(
                                            text = filterType.displayName,
                                            color = TextPrimary,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Switch(
                                            checked = isFilterActive,
                                            onCheckedChange = { active ->
                                                focusModeViewModel.updateFilterState(platform, filterType, active)
                                            },
                                            modifier = Modifier.testTag("switch_filter_${filterType.id}"),
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = AccentIndigo,
                                                uncheckedThumbColor = TextMuted,
                                                uncheckedTrackColor = DarkSurfaceElevated
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = SubtleBorderColor.copy(alpha = 0.2f), thickness = 1.dp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            TextButton(
                                onClick = { focusModeViewModel.resetSettings() },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("btn_reset_focus_filters"),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = AccentIndigoLight
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Reset Focus Filters",
                                        color = AccentIndigoLight,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 8. SAVE ACTION BUTTON
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.updateSettings(
                            userSettings.copy(
                                aiDigestsEnabled = aiDigestsEnabled,
                                notificationStyle = com.example.model.NotificationStyle.valueOf(notificationStyle),
                                syncFrequencyMinutes = syncFreq,
                                interestTopics = interestTopics,
                                activityFilters = activityFilters,
                                morningBriefingTime = morningBriefingTime
                            )
                        )
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Settings saved successfully!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_settings_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    // --- DIALOGS ---

    // Time Picker Custom Dialog
    if (showTimePickerDialog) {
        var tempHour by remember { mutableIntStateOf(morningBriefingTime.split(":").firstOrNull()?.toIntOrNull() ?: 8) }
        var tempMinute by remember { mutableIntStateOf(morningBriefingTime.split(":").lastOrNull()?.toIntOrNull() ?: 0) }
        
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = { Text("Morning Briefing Time", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Set delivery hour & minute:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Hour Selector Card
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { tempHour = (tempHour + 1) % 24 }) {
                                Icon(Icons.Default.KeyboardArrowUp, "Increment Hour", tint = AccentIndigoLight)
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                modifier = Modifier.width(64.dp)
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = String.format("%02d", tempHour),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            IconButton(onClick = { tempHour = if (tempHour - 1 < 0) 23 else tempHour - 1 }) {
                                Icon(Icons.Default.KeyboardArrowDown, "Decrement Hour", tint = AccentIndigoLight)
                            }
                        }
                        
                        Text(":", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(horizontal = 12.dp))
                        
                        // Minute Selector Card
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { tempMinute = (tempMinute + 5) % 60 }) {
                                Icon(Icons.Default.KeyboardArrowUp, "Increment Minute", tint = AccentIndigoLight)
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                modifier = Modifier.width(64.dp)
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = String.format("%02d", tempMinute),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            IconButton(onClick = { tempMinute = if (tempMinute - 5 < 0) 55 else tempMinute - 5 }) {
                                Icon(Icons.Default.KeyboardArrowDown, "Decrement Minute", tint = AccentIndigoLight)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        morningBriefingTime = String.format("%02d:%02d", tempHour, tempMinute)
                        showTimePickerDialog = false
                    }
                ) {
                    Text("OK", color = AccentIndigoLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Add Topic Custom Dialog
    if (showAddTopicDialog) {
        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("Add Interest Topic", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newTopicText,
                    onValueChange = { newTopicText = it },
                    label = { Text("Topic Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentIndigo,
                        unfocusedBorderColor = SubtleBorderColor,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        cursorColor = AccentIndigo
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_topic_input")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTopicText.isNotBlank()) {
                            interestTopics = interestTopics + newTopicText.trim()
                            newTopicText = ""
                        }
                        showAddTopicDialog = false
                    }
                ) {
                    Text("Add", color = AccentIndigoLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        newTopicText = ""
                        showAddTopicDialog = false
                    }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
