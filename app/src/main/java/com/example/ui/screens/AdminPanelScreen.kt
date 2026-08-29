package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FirebaseSyncRepository
import com.example.data.VideoRepository
import com.example.model.DevotionalVideo
import com.example.model.LanguageEnum
import com.example.model.RosarySchedule
import com.example.ui.theme.*
import com.example.viewmodel.RosaryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: RosaryViewModel,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { FirebaseSyncRepository() }
    val videoRepository = remember { VideoRepository(context) }
    val scope = rememberCoroutineScope()
    
    var selectedTab by remember { mutableStateOf(0) } // 0: Schedule, 1: YouTube Videos
    var schedule by remember { mutableStateOf(RosarySchedule()) }
    var showSavedMessage by remember { mutableStateOf(false) }

    var baseCountInput by remember { mutableStateOf(schedule.basePrayingCount.toString()) }
    var minFlucInput by remember { mutableStateOf(schedule.minPrayingCount.toString()) }
    var maxFlucInput by remember { mutableStateOf(schedule.maxPrayingCount.toString()) }

    // YouTube State
    val videos by videoRepository.observeVideos().collectAsState(initial = emptyList())
    var showAddVideoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.getSchedule().collect { fetched ->
            if (fetched != null) {
                schedule = fetched
                baseCountInput = fetched.basePrayingCount.toString()
                minFlucInput = fetched.minPrayingCount.toString()
                maxFlucInput = fetched.maxPrayingCount.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = "ADMIN CONTROL PANEL",
                        style = MaterialTheme.typography.titleMedium,
                        color = SacredGold,
                        fontWeight = FontWeight.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SacredBlack,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = SacredBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SacredCardBg,
                contentColor = SacredGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SacredGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedules & Live", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = SacredGold,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("YouTube & Streams", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = SacredGold,
                    unselectedContentColor = TextSecondary
                )
            }

            if (selectedTab == 0) {
                // Schedule Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SacredCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = SacredGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Daily Schedule Timings (24-Hour HH:MM)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = SacredGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Set start and end times for daily morning & evening live sessions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Morning Rosaries Section
                    Text(
                        text = "MORNING ROSARIES",
                        style = MaterialTheme.typography.labelLarge,
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )

                    TimeRangeInputRow("Morning Rosary 1", schedule.morning1Start, schedule.morning1End) { start, end ->
                        schedule = schedule.copy(morning1Start = start, morning1End = end)
                    }

                    TimeRangeInputRow("Morning Rosary 2", schedule.morning2Start, schedule.morning2End) { start, end ->
                        schedule = schedule.copy(morning2Start = start, morning2End = end)
                    }

                    TimeRangeInputRow("Morning Rosary 3", schedule.morning3Start, schedule.morning3End) { start, end ->
                        schedule = schedule.copy(morning3Start = start, morning3End = end)
                    }

                    // Evening Rosaries Section
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "EVENING ROSARIES",
                        style = MaterialTheme.typography.labelLarge,
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )

                    TimeRangeInputRow("Evening Rosary 1", schedule.evening1Start, schedule.evening1End) { start, end ->
                        schedule = schedule.copy(evening1Start = start, evening1End = end)
                    }

                    TimeRangeInputRow("Evening Rosary 2", schedule.evening2Start, schedule.evening2End) { start, end ->
                        schedule = schedule.copy(evening2Start = start, evening2End = end)
                    }

                    TimeRangeInputRow("Evening Rosary 3", schedule.evening3Start, schedule.evening3End) { start, end ->
                        schedule = schedule.copy(evening3Start = start, evening3End = end)
                    }

                    // Participant Count Configuration
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "LIVE PARTICIPANTS DISPLAY SETTINGS",
                        style = MaterialTheme.typography.labelLarge,
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SacredCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Show Participant Count on Live Card",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = schedule.showPrayingCount,
                                    onCheckedChange = { schedule = schedule.copy(showPrayingCount = it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SacredGold,
                                        checkedTrackColor = SacredGoldLight.copy(alpha = 0.5f),
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = SacredBlueLight
                                    )
                                )
                            }

                            if (schedule.showPrayingCount) {
                                OutlinedTextField(
                                    value = baseCountInput,
                                    onValueChange = {
                                        baseCountInput = it
                                        it.toIntOrNull()?.let { count ->
                                            schedule = schedule.copy(basePrayingCount = count)
                                        }
                                    },
                                    label = { Text("Base Participant Count") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = SacredGold,
                                        unfocusedBorderColor = SacredBlueLight
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = minFlucInput,
                                        onValueChange = {
                                            minFlucInput = it
                                            it.toIntOrNull()?.let { count ->
                                                schedule = schedule.copy(minPrayingCount = count)
                                            }
                                        },
                                        label = { Text("Min Fluctuation (-)") },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = SacredGold,
                                            unfocusedBorderColor = SacredBlueLight
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = maxFlucInput,
                                        onValueChange = {
                                            maxFlucInput = it
                                            it.toIntOrNull()?.let { count ->
                                                schedule = schedule.copy(maxPrayingCount = count)
                                            }
                                        },
                                        label = { Text("Max Fluctuation (+)") },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = SacredGold,
                                            unfocusedBorderColor = SacredBlueLight
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }

                    // Save Button
                    Button(
                        onClick = {
                            scope.launch {
                                repository.saveSchedule(schedule)
                                showSavedMessage = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SacredGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE SCHEDULE & BROADCAST",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    if (showSavedMessage) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = StatusSuccessGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessGreen)
                        ) {
                            Text(
                                text = "✓ Settings saved successfully to Firestore!",
                                color = StatusSuccessGreen,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            } else {
                // YouTube Videos Management Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SacredCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VideoLibrary,
                                        contentDescription = null,
                                        tint = SacredGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "YouTube & Streams (${videos.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = SacredGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = { showAddVideoDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SacredGold,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Add Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Text(
                                text = "Add live streams or video links from YouTube (e.g. Daily Rosary, Holy Mass, Hymns). Videos appear in the 'Watch Videos' section.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }

                    // Video list items
                    if (videos.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = SacredCardBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No videos added yet.", color = TextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showAddVideoDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = SacredGold, contentColor = Color.Black)
                                ) {
                                    Text("Add First YouTube Video")
                                }
                            }
                        }
                    } else {
                        videos.forEach { video ->
                            AdminVideoItemCard(
                                video = video,
                                onDelete = {
                                    videoRepository.deleteVideo(video.id)
                                    Toast.makeText(context, "Video removed", Toast.LENGTH_SHORT).show()
                                },
                                onToggleLive = {
                                    val updated = video.copy(isLive = !video.isLive)
                                    videoRepository.saveVideo(updated)
                                },
                                onToggleFeatured = {
                                    val updated = video.copy(isFeatured = !video.isFeatured)
                                    videoRepository.saveVideo(updated)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    if (showAddVideoDialog) {
        AddYouTubeVideoDialog(
            currentLanguage = LanguageEnum.HINDI,
            onDismiss = { showAddVideoDialog = false },
            onSave = { newVideo ->
                videoRepository.saveVideo(newVideo)
                showAddVideoDialog = false
                Toast.makeText(context, "YouTube link added successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun AdminVideoItemCard(
    video: DevotionalVideo,
    onDelete: () -> Unit,
    onToggleLive: () -> Unit,
    onToggleFeatured: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SacredCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 55.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black)
                ) {
                    if (video.thumbnailUrl.isNotBlank()) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    if (video.isLive) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = Color(0xFFE50914),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(2.dp)
                        ) {
                            Text("LIVE", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = video.category.englishName,
                            color = SacredGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${video.language.name}",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFFF6B6B))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggles row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live toggle button
                OutlinedButton(
                    onClick = onToggleLive,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (video.isLive) Color(0xFF8B1A1A) else Color.Transparent,
                        contentColor = if (video.isLive) Color.White else TextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (video.isLive) Color(0xFFE50914) else SacredBlueLight
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(if (video.isLive) "🔴 Live Active" else "Set as Live", fontSize = 11.sp)
                }

                // Featured toggle
                OutlinedButton(
                    onClick = onToggleFeatured,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (video.isFeatured) SacredGold.copy(alpha = 0.2f) else Color.Transparent,
                        contentColor = if (video.isFeatured) SacredGold else TextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (video.isFeatured) SacredGold else SacredBlueLight
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(if (video.isFeatured) "⭐ Featured" else "Set Featured", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun TimeRangeInputRow(
    label: String,
    startTime: String,
    endTime: String,
    onValueChange: (String, String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SacredCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { onValueChange(it, endTime) },
                    label = { Text("Start Time") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = SacredGold,
                        unfocusedBorderColor = SacredBlueLight
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { onValueChange(startTime, it) },
                    label = { Text("End Time") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = SacredGold,
                        unfocusedBorderColor = SacredBlueLight
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        }
    }
}
