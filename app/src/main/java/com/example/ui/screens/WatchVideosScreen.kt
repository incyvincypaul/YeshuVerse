package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.VideoRepository
import com.example.model.DevotionalVideo
import com.example.model.LanguageEnum
import com.example.model.VideoCategory
import com.example.ui.components.YouTubePlayerDialog
import com.example.ui.theme.*
import com.example.viewmodel.RosaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchVideosScreen(
    viewModel: RosaryViewModel,
    currentLanguage: LanguageEnum,
    onBackClick: () -> Unit = {},
    onOpenAdmin: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { VideoRepository(context) }
    val videos by repository.observeVideos().collectAsState(initial = emptyList())

    var selectedCategory by remember { mutableStateOf(VideoCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var activePlayingVideo by remember { mutableStateOf<DevotionalVideo?>(null) }

    val filteredVideos = remember(videos, selectedCategory, searchQuery, currentLanguage) {
        videos.filter { video ->
            val matchesCategory = (selectedCategory == VideoCategory.ALL) || 
                (selectedCategory == VideoCategory.LIVE_STREAM && video.isLive) ||
                (video.category == selectedCategory)
            
            val matchesSearch = searchQuery.isBlank() || 
                video.title.contains(searchQuery, ignoreCase = true) || 
                video.description.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    val featuredVideo = remember(videos) {
        videos.firstOrNull { it.isFeatured } ?: videos.firstOrNull { it.isLive } ?: videos.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                title = {
                    val isAnyLive = videos.any { it.isLive }
                    Column {
                        Text(
                            text = if (isAnyLive) {
                                when (currentLanguage) {
                                    LanguageEnum.HINDI -> "🔴 लाइव दर्शन व भक्ति वीडियो"
                                    LanguageEnum.MALAYALAM -> "🔴 തത്സമയ ദർശനവും വീഡിയോകളും"
                                    else -> "🔴 LIVE STREAM & DEVOTIONAL"
                                }
                            } else {
                                when (currentLanguage) {
                                    LanguageEnum.HINDI -> "भक्ति वीडियो व भजन"
                                    LanguageEnum.MALAYALAM -> "ഭക്തി വീഡിയോകളും ഗാനങ്ങളും"
                                    else -> "DEVOTIONAL VIDEOS & HYMNS"
                                }
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "पवित्र रोज़री, मिस्सा व मसीही भजन"
                                LanguageEnum.MALAYALAM -> "വിശുദ്ധ ജപമാല, കുർബാന, ഭക്തിഗാനങ്ങൾ"
                                else -> "Holy Rosary, Holy Mass & Hymns"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBlack,
                    titleContentColor = TextWhite
                )
            )
        },
        containerColor = AppBlack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "वीडियो या भजन खोजें..."
                                LanguageEnum.MALAYALAM -> "വീഡിയോകൾ തിരയുക..."
                                else -> "Search devotional videos & hymns..."
                            },
                            color = TextGray,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Gold
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextGray)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = Color(0xFF2A364F),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )
            }

            // Featured / Live Stream Hero Card
            if (featuredVideo != null && searchQuery.isBlank() && selectedCategory == VideoCategory.ALL) {
                item {
                    FeaturedHeroVideoCard(
                        video = featuredVideo,
                        currentLanguage = currentLanguage,
                        onPlayClick = { activePlayingVideo = featuredVideo }
                    )
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(VideoCategory.values()) { category ->
                        val isSelected = category == selectedCategory
                        val title = when (currentLanguage) {
                            LanguageEnum.HINDI -> category.hindiName
                            LanguageEnum.MALAYALAM -> category.malayalamName
                            else -> category.englishName
                        }

                        Surface(
                            onClick = { selectedCategory = category },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Gold else Color(0xFF131D2E),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Gold else Color(0xFF2A3A55)
                            )
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TextWhite,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "भक्ति वीडियो संग्रह (${filteredVideos.size})"
                            LanguageEnum.MALAYALAM -> "ഭക്തി വീഡിയോകൾ (${filteredVideos.size})"
                            else -> "Devotional Videos (${filteredVideos.size})"
                        },
                        fontSize = 15.sp,
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "टैप करके देखें"
                            LanguageEnum.MALAYALAM -> "കാണാൻ ടാപ്പ് ചെയ്യുക"
                            else -> "Tap to Watch"
                        },
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }

            // Videos List
            if (filteredVideos.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A364F))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideocamOff,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "कोई वीडियो नहीं मिला"
                                    LanguageEnum.MALAYALAM -> "വീഡിയോകൾ ഒന്നും കണ്ടെത്തിയില്ല"
                                    else -> "No videos found"
                                },
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "शीघ्र ही यहाँ नए लाइव प्रसारण, पवित्र मिस्सा व भक्ति वीडियो उपलब्ध होंगे।"
                                    LanguageEnum.MALAYALAM -> "ഉടൻ തന്നെ പുതിയ തത്സമയ പ്രക്ഷേപണങ്ങളും ഭക്തിഗാനങ്ങളും ഇവിടെ ലഭ്യമാകും."
                                    else -> "New devotional broadcasts, Holy Mass & hymns will be available soon."
                                },
                                color = TextGray,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredVideos, key = { it.id }) { video ->
                    DevotionalVideoItemCard(
                        video = video,
                        currentLanguage = currentLanguage,
                        onClick = { activePlayingVideo = video }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Active Video Player Modal
    activePlayingVideo?.let { video ->
        YouTubePlayerDialog(
            video = video,
            currentLanguage = currentLanguage,
            onDismiss = { activePlayingVideo = null }
        )
    }
}

@Composable
fun FeaturedHeroVideoCard(
    video: DevotionalVideo,
    currentLanguage: LanguageEnum,
    onPlayClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onPlayClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0C1322),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Gold.copy(alpha = 0.8f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Thumbnail Image
            if (video.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dark Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x88000000),
                                Color(0xF0000000)
                            )
                        )
                    )
            )

            // Live / Featured Tag (Top Left)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (video.isLive) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE50914)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "🔴 LIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xCC000000)
                ) {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> video.category.hindiName
                            LanguageEnum.MALAYALAM -> video.category.malayalamName
                            else -> video.category.englishName
                        },
                        color = Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Centered Play Button
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Gold)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Bottom Title & Description
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    lineHeight = 20.sp
                )
                if (video.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = video.description,
                        color = TextGray,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun DevotionalVideoItemCard(
    video: DevotionalVideo,
    currentLanguage: LanguageEnum,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video Thumbnail
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(75.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                if (video.thumbnailUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(video.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Play icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Gold,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (video.isLive) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = Color(0xFFE50914),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                // Category badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> video.category.hindiName
                                LanguageEnum.MALAYALAM -> video.category.malayalamName
                                else -> video.category.englishName
                            },
                            color = Gold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (video.language != LanguageEnum.ENGLISH) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = video.language.nativeName,
                            color = TextGray,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.title,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    lineHeight = 17.sp
                )

                if (video.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = video.description,
                        color = TextGray,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun AddYouTubeVideoDialog(
    currentLanguage: LanguageEnum,
    onDismiss: () -> Unit,
    onSave: (DevotionalVideo) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var youtubeUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(VideoCategory.ROSARY) }
    var language by remember { mutableStateOf(currentLanguage) }
    var isLive by remember { mutableStateOf(false) }
    var isFeatured by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val videoId = remember(youtubeUrl) {
        DevotionalVideo.extractYouTubeVideoId(youtubeUrl)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (currentLanguage) {
                        LanguageEnum.HINDI -> "YouTube लिंक जोड़ें"
                        LanguageEnum.MALAYALAM -> "YouTube ലിങ്ക് ചേർക്കുക"
                        else -> "Add YouTube Video / Stream"
                    },
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // YouTube URL Input
                OutlinedTextField(
                    value = youtubeUrl,
                    onValueChange = {
                        youtubeUrl = it
                        errorText = ""
                    },
                    label = { Text("YouTube URL or Video ID", color = Gold) },
                    placeholder = { Text("https://youtu.be/... or watch?v=...", color = TextGray, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Thumbnail Preview
                if (videoId.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = "https://img.youtube.com/vi/$videoId/hqdefault.jpg",
                            contentDescription = "Thumbnail Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xCC000000),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "Video ID: $videoId",
                                color = Gold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorText = ""
                    },
                    label = { Text(when (currentLanguage) { LanguageEnum.HINDI -> "वीडियो का शीर्षक (Title)"; LanguageEnum.MALAYALAM -> "തലക്കെട്ട് (Title)"; else -> "Title" }, color = Gold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Category Selection Chips
                Text(
                    text = when (currentLanguage) { LanguageEnum.HINDI -> "श्रेणी (Category):"; LanguageEnum.MALAYALAM -> "വിഭാഗം (Category):"; else -> "Category:" },
                    color = TextGray,
                    fontSize = 12.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf(VideoCategory.ROSARY, VideoCategory.HOLY_MASS, VideoCategory.HYMNS, VideoCategory.SERMON, VideoCategory.NOVENA)) { cat ->
                        val isCatSelected = cat == category
                        Surface(
                            onClick = { category = cat },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCatSelected) Gold else Color(0xFF1E293B)
                        ) {
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> cat.hindiName
                                    LanguageEnum.MALAYALAM -> cat.malayalamName
                                    else -> cat.englishName
                                },
                                color = if (isCatSelected) Color.Black else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Toggles: Live & Featured
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isLive,
                            onCheckedChange = { isLive = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE50914))
                        )
                        Text(
                            text = "🔴 LIVE Stream",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isFeatured,
                            onCheckedChange = { isFeatured = it },
                            colors = CheckboxDefaults.colors(checkedColor = Gold)
                        )
                        Text(
                            text = "⭐ Featured",
                            color = Gold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (errorText.isNotBlank()) {
                    Text(
                        text = errorText,
                        color = Color(0xFFFF6B6B),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (youtubeUrl.isBlank() || videoId.isBlank()) {
                        errorText = "कृपया सही YouTube लिंक या Video ID दर्ज करें।"
                        return@Button
                    }
                    if (title.isBlank()) {
                        errorText = "कृपया वीडियो का शीर्षक दर्ज करें।"
                        return@Button
                    }

                    val video = DevotionalVideo(
                        title = title.trim(),
                        youtubeUrl = youtubeUrl.trim(),
                        category = category,
                        language = language,
                        isLive = isLive,
                        isFeatured = isFeatured,
                        description = description.trim()
                    )
                    onSave(video)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when (currentLanguage) {
                        LanguageEnum.HINDI -> "सेव करें (Save)"
                        LanguageEnum.MALAYALAM -> "സേവ് ചെയ്യുക"
                        else -> "Save Video"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = when (currentLanguage) {
                        LanguageEnum.HINDI -> "रद्द करें"
                        LanguageEnum.MALAYALAM -> "റദ്ദാക്കുക"
                        else -> "Cancel"
                    },
                    color = TextGray
                )
            }
        }
    )
}
