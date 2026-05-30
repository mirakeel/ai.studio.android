package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ContentItem
import com.example.model.Platform
import com.example.model.RecommendationResult
import com.example.repository.RecommendationEngine
import com.example.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RelatedContentBottomSheet(
    onDismissRequest: () -> Unit,
    initialViewedItem: ContentItem = RecommendationEngine.sampleViewedItems.first(),
    modifier: Modifier = Modifier
) {
    // Current viewed item in context
    var currentViewedItem by remember { mutableStateOf(initialViewedItem) }

    // User interest preference state
    var userInterests by remember {
        mutableStateOf(
            mapOf(
                "python" to 0.7f,
                "backend" to 0.7f,
                "fastapi" to 0.8f,
                "android" to 0.6f,
                "compose" to 0.7f,
                "ui" to 0.5f,
                "crypto" to 0.4f,
                "defi" to 0.5f,
                "finance" to 0.4f,
                "sleep" to 0.7f,
                "health" to 0.6f,
                "performance" to 0.5f
            )
        )
    }

    // Hidden topics list to filter out content dynamically
    var hiddenTopics by remember { mutableStateOf(setOf<String>()) }

    // Feedback alert log to display when user performs actions
    var actionLogMessage by remember { mutableStateOf<String?>(null) }

    // Recommendations state recalculated reactively
    val recommendations = remember(currentViewedItem, userInterests, hiddenTopics) {
        // Filter out any items in the pool that contain hidden topics
        val originalPool = RecommendationEngine.mockPool
        val filteredRecommendations = RecommendationEngine.recommend(
            viewedItem = currentViewedItem,
            userInterests = userInterests,
            limit = 6
        ).filter { rec ->
            // Exclude if any candidate topic is in the hiddenTopics set
            rec.item.topics.none { it.trim().lowercase() in hiddenTopics }
        }.take(3) // Ensure we output the top 3 corresponding to user query
        
        filteredRecommendations
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = DarkBackground,
        dragHandle = { BottomSheetDefaults.DragHandle(color = SubtleBorderColor) },
        modifier = modifier.testTag("related_content_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            // Header Content Block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Related Content Feed",
                        tint = AccentIndigoLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Related Content Analyzer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Based on: ${currentViewedItem.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentIndigoLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("btn_close_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Sheet",
                        tint = TextSecondary
                    )
                }
            }

            // Action notification status badge
            AnimatedVisibility(visible = actionLogMessage != null) {
                actionLogMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentIndigoDark.copy(alpha = 0.4f))
                            .border(0.5.dp, AccentIndigoLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active Filter Confirm",
                                tint = AccentIndigoLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Auto-dismiss or reset helper trigger
                    LaunchedEffect(msg) {
                        kotlinx.coroutines.delay(2500)
                        if (actionLogMessage == msg) {
                            actionLogMessage = null
                        }
                    }
                }
            }

            // Lazy List showing the recommendations and interactable filters
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Topic Preferences Header Summary Status
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, SubtleBorderColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Affinities & Active Filters",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Active Focus Interests", fontSize = 10.sp, color = TextSecondary)
                                val highInterests = userInterests.filter { it.value > 0.5f }.keys.sorted().take(4)
                                Text(
                                    text = if (highInterests.isEmpty()) "None" else highInterests.joinToString { it.uppercase() },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentIndigoLight,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(SubtleBorderColor))
                            
                            Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                                Text("Excluded Topics", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    text = if (hiddenTopics.isEmpty()) "None Active" else hiddenTopics.joinToString { it.uppercase() },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                if (recommendations.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, SubtleBorderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Empty list due to filters",
                                    tint = TextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "All recommendations filtered out",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Try clearing hidden topics or adjusting interest weights to discover content.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.clickable {
                                        hiddenTopics = emptySet()
                                        actionLogMessage = "Cleared all excluded topics."
                                    },
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                } else {
                    items(recommendations) { rec ->
                        BottomSheetRecommendationCard(
                            recommendation = rec,
                            onMoreLikeThis = {
                                // Boost all associated topic weights
                                val updated = userInterests.toMutableMap()
                                rec.item.topics.forEach { topic ->
                                    val key = topic.trim().lowercase()
                                    val currentWeight = updated[key] ?: 0.5f
                                    updated[key] = (currentWeight + 0.15f).coerceAtMost(1.0f)
                                }
                                userInterests = updated
                                actionLogMessage = "More like: '${rec.item.title}' (Topics boosted)."
                            },
                            onLessLikeThis = {
                                // Decrease all associated topic weights
                                val updated = userInterests.toMutableMap()
                                rec.item.topics.forEach { topic ->
                                    val key = topic.trim().lowercase()
                                    val currentWeight = updated[key] ?: 0.5f
                                    updated[key] = (currentWeight - 0.20f).coerceAtLeast(0.0f)
                                }
                                userInterests = updated
                                actionLogMessage = "Less like: '${rec.item.title}' (Topics reduced)."
                            },
                            onHideTopic = { topicToHide ->
                                val key = topicToHide.trim().lowercase()
                                hiddenTopics = hiddenTopics + key
                                actionLogMessage = "Topic '$topicToHide' is now fully hidden."
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BottomSheetRecommendationCard(
    recommendation: RecommendationResult,
    onMoreLikeThis: () -> Unit,
    onLessLikeThis: () -> Unit,
    onHideTopic: (String) -> Unit
) {
    val content = recommendation.item
    
    val brandColor = when (content.platform) {
        Platform.INSTAGRAM -> ColorInstagram
        Platform.X -> Color.White
        Platform.YOUTUBE -> ColorYoutube
    }

    val platformIcon = when (content.platform) {
        Platform.INSTAGRAM -> Icons.Default.CameraAlt
        Platform.X -> Icons.Default.Close
        Platform.YOUTUBE -> Icons.Default.PlayArrow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sheet_recommendation_${content.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Brand and Match Bubble
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
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(brandColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = platformIcon,
                            contentDescription = content.platform.name,
                            tint = if (content.platform == Platform.X) Color.White else brandColor,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Text(
                        text = content.platform.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandColor
                    )
                }

                // Match strength percentage
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentIndigo.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${(recommendation.finalScore * 100).roundToInt()}% Match",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentIndigoLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Content title and descriptions
            Text(
                text = content.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Topic Badges with individual "Hide" tag clickability
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                content.topics.forEach { topic ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceElevated)
                            .border(0.5.dp, SubtleBorderColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topic,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // Tapping "x" icon hides this specific topic tag
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hide '$topic'",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .clickable { onHideTopic(topic) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recommendation Reason string
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkSurfaceElevated)
                    .padding(8.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AccentIndigoLight,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = recommendation.recommendationReason.description,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ACTION CONTROLS: "More Like This", "Less Like This"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onMoreLikeThis,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("action_more_like_${content.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentIndigoDark,
                        contentColor = AccentIndigoLight
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("More Like This", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onLessLikeThis,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("action_less_like_${content.id}"),
                    border = BorderStroke(1.dp, SubtleBorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Less Like This", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
