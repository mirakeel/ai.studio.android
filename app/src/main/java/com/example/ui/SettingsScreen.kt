package com.example.ui

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.viewmodel.SocialDashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SocialDashViewModel = viewModel()
) {
    // Collect State
    val userSettings by viewModel.settings.collectAsState()
    
    // Local State mapped to UI
    var aiDigestsEnabled by remember { mutableStateOf(userSettings.aiDigestsEnabled) }
    var notificationStyle by remember { mutableStateOf(userSettings.notificationStyle.name) }
    var syncFreq by remember { mutableIntStateOf(userSettings.syncFrequencyMinutes) }
    var interestTopics by remember { mutableStateOf(userSettings.interestTopics) }
    var activityFilters by remember { mutableStateOf(userSettings.activityFilters) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("AI DIGESTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentIndigoLight)
                com.example.model.Platform.values().forEach { platform ->
                    val isChecked = aiDigestsEnabled[platform] ?: false
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = DarkSurfaceElevated),
                        headlineContent = { Text(platform.name) },
                        trailingContent = {
                            Switch(checked = isChecked, onCheckedChange = {
                                aiDigestsEnabled = aiDigestsEnabled + (platform to it)
                            })
                        }
                    )
                }
            }

            item {
                Text("NOTIFICATIONS & SYNC", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentIndigoLight)
                Spacer(modifier = Modifier.height(8.dp))
                // Simple placeholder selections
                OutlinedTextField(value = notificationStyle, onValueChange = {}, readOnly = true, label = { Text("Notification Style") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = syncFreq.toString(), onValueChange = {}, readOnly = true, label = { Text("Sync Frequency") }, modifier = Modifier.fillMaxWidth())
            }

            item {
                Text("INTEREST TOPICS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentIndigoLight)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    interestTopics.forEach { topic ->
                        FilterChip(
                            selected = true,
                            onClick = { interestTopics = interestTopics - topic },
                            label = { Text(topic) },
                            trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    FilterChip(selected = false, onClick = {}, label = { Text("+ Add") }, leadingIcon = { Icon(Icons.Default.Add, null) })
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.updateSettings(
                            userSettings.copy(
                                aiDigestsEnabled = aiDigestsEnabled,
                                // Assuming notificationStyle name is in NotificationStyle enum
                                notificationStyle = com.example.model.NotificationStyle.valueOf(notificationStyle),
                                syncFrequencyMinutes = syncFreq,
                                interestTopics = interestTopics,
                                activityFilters = activityFilters
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

