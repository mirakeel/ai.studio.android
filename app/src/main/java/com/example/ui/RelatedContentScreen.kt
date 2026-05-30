package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RelatedContentScreen(
    onBackClick: () -> Unit
) {
    // 1. Interactive States
    var selectedViewedIdx by remember { mutableIntStateOf(0) }
    val currentViewedItem = RecommendationEngine.sampleViewedItems[selectedViewedIdx]

    // Local adjustable interest weights for calculations in real-time
    var interestWeights by remember {
        mutableStateOf(
            mapOf(
                "python" to 0.9f,
                "backend" to 0.9f,
                "fastapi" to 0.8f,
                "android" to 0.5f,
                "compose" to 0.7f,
                "ui" to 0.6f,
                "crypto" to 0.4f,
                "defi" to 0.5f,
                "finance" to 0.4f,
                "sleep" to 0.8f,
                "health" to 0.7f,
                "performance" to 0.6f
            )
        )
    }

    // Auto recalculations when viewed article or weights change
    val recommendations = remember(currentViewedItem, interestWeights) {
        RecommendationEngine.recommend(
            viewedItem = currentViewedItem,
            userInterests = interestWeights,
            limit = 3
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Related Content Engine", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_related_back")) {
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            
            // 1. Header Information Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("card_engine_info"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = AccentIndigoLight, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Intelligent Recommendation Logic",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "This engine balances two inputs: Topic Matching (using Jaccard similarity index on content tags) and your personalized Interest Profile weights. Drag sliders to witness real-time recommendation updates.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 2. VIEWED ITEM SELECTOR CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("card_select_viewed"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Active Viewed Content",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentIndigoLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Row of available Viewed items
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(RecommendationEngine.sampleViewedItems.size) { idx ->
                                val item = RecommendationEngine.sampleViewedItems[idx]
                                val isSelected = idx == selectedViewedIdx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) AccentIndigo.copy(alpha = 0.8f) else DarkSurfaceElevated)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) AccentIndigo else SubtleBorderColor,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedViewedIdx = idx }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .testTag("chip_viewed_$idx"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.title,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Detail of currently viewed item
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = currentViewedItem.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    val platformBadgeColor = when (currentViewedItem.platform) {
                                        Platform.INSTAGRAM -> ColorInstagram
                                        Platform.X -> Color.White
                                        Platform.YOUTUBE -> ColorYoutube
                                    }
                                    Text(
                                        text = currentViewedItem.platform.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = platformBadgeColor,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(platformBadgeColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentViewedItem.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Topic Chips
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    currentViewedItem.topics.forEach { topic ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AccentIndigoDark.copy(alpha = 0.3f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = topic,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = AccentIndigoLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. USER INTEREST PROFILE SLIDERS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("card_interest_sliders"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, SubtleBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "2. Active Interest Weights",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentIndigoLight
                            )
                            IconButton(
                                onClick = {
                                    // Reset local sliders to classic configuration
                                    interestWeights = interestWeights.mapValues { 0.5f }
                                },
                                modifier = Modifier.size(24.dp).testTag("btn_reset_weights")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset weights", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Fine-tune your personal topic affinity score weights used for interest calculations:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Render Sliders for viewed item's primary tags first, followed by others
                        val coreTopics = (currentViewedItem.topics + listOf("Python", "Backend", "FastAPI", "Android", "Compose", "Sleep", "Health", "DeFi")).distinct()
                        
                        coreTopics.forEach { topic ->
                            val key = topic.lowercase()
                            val weight = interestWeights[key] ?: 0.5f

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isCoreViewed = topic in currentViewedItem.topics
                                    Text(
                                        text = "$topic ${if (isCoreViewed) "★" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isCoreViewed) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCoreViewed) Color.White else TextSecondary
                                    )
                                    Text(
                                        text = "${(weight * 100).roundToInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (weight >= 0.7f) AccentIndigoLight else TextMuted
                                    )
                                }
                                Slider(
                                    value = weight,
                                    onValueChange = { newValue ->
                                        val m = interestWeights.toMutableMap()
                                        m[key] = newValue
                                        interestWeights = m
                                    },
                                    valueRange = 0f..1f,
                                    modifier = Modifier.height(24.dp).testTag("slider_$key"),
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentIndigo,
                                        activeTrackColor = AccentIndigoLight,
                                        inactiveTrackColor = DarkSurfaceElevated
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. GENERATED RELATED RECOMMENDATIONS LIST
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3. Recommended Similar Content",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Top ${recommendations.size} Ranked",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            if (recommendations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No matching recommendations found.", color = TextMuted)
                    }
                }
            } else {
                items(recommendations) { item ->
                    RecommendationItemRow(
                        recommendation = item,
                        currentViewedItem = currentViewedItem
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecommendationItemRow(
    recommendation: RecommendationResult,
    currentViewedItem: ContentItem
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
            .testTag("card_recommendation_${content.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, SubtleBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title and Platform Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(brandColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = platformIcon,
                                contentDescription = content.platform.name,
                                tint = if (content.platform == Platform.X) Color.White else brandColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = content.platform.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Composite final score badge bubble
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentIndigo.copy(alpha = 0.2f))
                        .border(1.dp, AccentIndigo, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(recommendation.finalScore * 100).roundToInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = AccentIndigoLight
                        )
                        Text(
                            text = "MATCH",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            
            // Score split breakdown metrics
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("similarity score", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text("${(recommendation.similarityScore * 100).roundToInt()}% Overlap", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Box(modifier = Modifier.height(24.dp).width(1.dp).background(SubtleBorderColor))
                Column {
                    Text("interest score", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text("${(recommendation.interestScore * 100).roundToInt()}% Preference", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Topic elements highlight matching vs non-matching
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                content.topics.forEach { topic ->
                    val isMatching = topic.lowercase() in currentViewedItem.topics.map { it.lowercase() }
                    val bubbleColor = if (isMatching) AccentIndigo.copy(alpha = 0.15f) else DarkSurfaceElevated
                    val textColor = if (isMatching) AccentIndigoLight else TextSecondary
                    val labelPrefix = if (isMatching) "✓ $topic" else topic
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(bubbleColor)
                            .then(
                                if (isMatching) Modifier.border(0.5.dp, AccentIndigoLight.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                else Modifier
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = labelPrefix,
                            fontSize = 10.sp,
                            fontWeight = if (isMatching) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Dynamic recommendation reason string block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .border(0.5.dp, SubtleBorderColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Reason Info Tag",
                        tint = AccentIndigoLight,
                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                    )
                    Text(
                        text = recommendation.recommendationReason.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
