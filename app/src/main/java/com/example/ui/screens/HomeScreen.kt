package com.example.ui.screens

import android.widget.Toast

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.RosaryPrayers
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import com.example.data.VideoRepository
import com.example.model.LanguageEnum
import com.example.viewmodel.RosaryViewModel

val AppBlack = Color(0xFF000000)
val Gold = Color(0xFFD4AF37)
val GoldDim = Color(0xFF9CA3AF)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF9CA3AF)
val TextDarkGray = Color(0xFF4B5563)

val HeroLiveRed = Color(0xFFE53935)
val HeroLiveBg = Color(0x33E53935)

val LiveCardBorder = Color(0xFFD4AF37).copy(alpha = 0.8f)
val LiveCardBg = Color(0xFF070B11)
val LiveNowBg = Color(0xFF8B1A1A)

val SoloCardBorder = Color(0xFF1E293B)
val SoloCardBg = Color(0xFF040B16)
val SoloBlue = Color(0xFF60A5FA)
val SoloButtonBg = Color(0xFF0F172A)

val MysteryCardBorder = Color(0xFF1B3B24)
val MysteryCardBg = Color(0xFF051208)
val MysteryGreen = Color(0xFF86EFAC)
val MysteryButtonBg = Color(0xFF062B17)

val IntentionCardBorder = Color(0xFF3B1F54)
val IntentionCardBg = Color(0xFF0A0512)
val IntentionPurple = Color(0xFFD8B4FE)
val IntentionButtonBg = Color(0xFF261240)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RosaryViewModel,
    onJoinLiveRoom: () -> Unit,
    onStartSoloPrayer: () -> Unit,
    onOpenNovena: () -> Unit = {},
    onOpenWatchVideos: () -> Unit = {},
    onOpenAdmin: () -> Unit = {},
    onOpenAbout: () -> Unit = {}
) {
    val context = LocalContext.current
    val roomState by viewModel.roomState.collectAsState()
    val participantCount by viewModel.participantCount.collectAsState()
    val showPrayingCount by viewModel.showPrayingCount.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val activeSession by viewModel.activeSession.collectAsState()
    val nextUpcomingSession by viewModel.nextUpcomingSession.collectAsState()
    val isSessionLive = activeSession != null || roomState.isLive

    val scheduleState by viewModel.schedule.collectAsState()
    val currentSchedule = scheduleState ?: com.example.model.RosarySchedule()
    val savedUserName by viewModel.savedUserName.collectAsState()

    val videoRepository = remember { VideoRepository(context) }
    val devotionalVideos by videoRepository.observeVideos().collectAsState(initial = emptyList())
    val isAnyVideoLive = devotionalVideos.any { it.isLive }

    var selectedSessionToBook by remember { mutableStateOf<com.example.model.SessionItem?>(null) }
    var leaderNameInput by remember { mutableStateOf("") }

    var tapCount by remember { mutableStateOf(0) }
    var showAdminPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val todayDefaultMystery = RosaryPrayers.getTodayDefaultMystery()
    val mysteries = RosaryPrayers.getMysteriesForType(todayDefaultMystery)

    val liveCardProgress = remember { Animatable(0f) }
    val soloCardProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        liveCardProgress.animateTo(1f, animationSpec = tween(700))
    }
    LaunchedEffect(Unit) {
        delay(200)
        soloCardProgress.animateTo(1f, animationSpec = tween(700))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "†",
                            fontSize = 32.sp,
                            color = Gold,
                            modifier = Modifier.padding(end = 12.dp, bottom = 4.dp),
                            fontFamily = FontFamily.Serif
                        )
                        Column {
                            Text(
                                text = "YeshuVerse",
                                fontSize = 18.sp,
                                color = Gold,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "Live Rosary • प्रार्थना करें"; LanguageEnum.MALAYALAM -> "തത്സമയ ജപമാല • ഒരുമിച്ച് പ്രാർത്ഥിക്കാം"; else -> "Live Rosary • Pray Together" },
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                },
                actions = {
                    // Language Switch Button
                    Surface(
                        onClick = {
                            val nextLang = when (currentLanguage) { LanguageEnum.ENGLISH -> LanguageEnum.HINDI; LanguageEnum.HINDI -> LanguageEnum.MALAYALAM; else -> LanguageEnum.ENGLISH }
                            viewModel.setLanguage(nextLang)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.4f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = Gold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentLanguage.nativeName,
                                fontSize = 13.sp,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Notifications
                    IconButton(
                        onClick = { onOpenAbout() },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .border(1.dp, Gold.copy(alpha = 0.4f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Gold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBlack,
                    titleContentColor = TextWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AppBlack,
                contentColor = Gold,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(when (currentLanguage) { LanguageEnum.HINDI -> "होम"; LanguageEnum.MALAYALAM -> "ഹോം"; else -> "Home" }, fontSize = 11.sp) },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold,
                        selectedTextColor = Gold,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = TextDarkGray,
                        unselectedTextColor = TextDarkGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Watch Videos") },
                    label = { Text(when (currentLanguage) { LanguageEnum.HINDI -> "वीडियो"; LanguageEnum.MALAYALAM -> "വീഡിയോ"; else -> "Watch" }, fontSize = 11.sp) },
                    selected = false,
                    onClick = { onOpenWatchVideos() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold,
                        selectedTextColor = Gold,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = TextDarkGray,
                        unselectedTextColor = TextDarkGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Toll, contentDescription = "Rosary") },
                    label = { Text(when (currentLanguage) { LanguageEnum.HINDI -> "रोज़री"; LanguageEnum.MALAYALAM -> "ജപമാല"; else -> "Rosary" }, fontSize = 11.sp) },
                    selected = false,
                    onClick = { onStartSoloPrayer() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold,
                        selectedTextColor = Gold,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = TextDarkGray,
                        unselectedTextColor = TextDarkGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = "Prayers") },
                    label = { Text(when (currentLanguage) { LanguageEnum.HINDI -> "प्रार्थनाएं"; LanguageEnum.MALAYALAM -> "പ്രാർത്ഥനകൾ"; else -> "Prayers" }, fontSize = 11.sp) },
                    selected = false,
                    onClick = { onOpenNovena() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold,
                        selectedTextColor = Gold,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = TextDarkGray,
                        unselectedTextColor = TextDarkGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.GridView, contentDescription = "More") },
                    label = { Text(when (currentLanguage) { LanguageEnum.HINDI -> "अधिक"; LanguageEnum.MALAYALAM -> "കൂടുതൽ"; else -> "More" }, fontSize = 11.sp) },
                    selected = false,
                    onClick = { onOpenAbout() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold,
                        selectedTextColor = Gold,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = TextDarkGray,
                        unselectedTextColor = TextDarkGray
                    )
                )
            }
        },
        containerColor = AppBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Hero Banner Card
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .graphicsLayer {
                        alpha = liveCardProgress.value
                        scaleX = 0.95f + (0.05f * liveCardProgress.value)
                        scaleY = 0.95f + (0.05f * liveCardProgress.value)
                    },
                colors = CardDefaults.cardColors(containerColor = LiveCardBg)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_rosary_bg_1786303294557),
                        contentDescription = "Hero Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = 0.9f
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xAA000000),
                                        Color(0xFF000000)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(if (isSessionLive) HeroLiveBg else Color(0xFF1E283A), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSessionLive) HeroLiveRed else Gold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSessionLive) {
                                    when (currentLanguage) { LanguageEnum.HINDI -> "लाइव"; LanguageEnum.MALAYALAM -> "തത്സമയം"; else -> "LIVE" }
                                } else {
                                    when (currentLanguage) { LanguageEnum.HINDI -> "निर्धारित"; LanguageEnum.MALAYALAM -> "ക്രമീകരിച്ചത്"; else -> "SCHEDULED" }
                                },
                                fontSize = 10.sp,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "लाइव पवित्र रोज़री"; LanguageEnum.MALAYALAM -> "തത്സമയ ജപമാല"; else -> "LIVE ROSARY" },
                            fontSize = 28.sp,
                            color = Gold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "एक साथ विश्वास में प्रार्थना करें"; LanguageEnum.MALAYALAM -> "വിശ്വാസത്തോടെ ഒരുമിച്ച് പ്രാർത്ഥിക്കാം"; else -> "Pray Together in Faith" },
                            fontSize = 14.sp,
                            color = TextWhite,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        if (showPrayingCount) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color(0x66000000), RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(modifier = Modifier.width(64.dp).height(24.dp)) {
                                    val avatars = listOf(Color(0xFF805A46), Color(0xFFC3A38A), Color(0xFFD49E8D), Color(0xFF5A443A))
                                    avatars.forEachIndexed { index, color ->
                                        Box(
                                            modifier = Modifier
                                                .padding(start = (index * 14).dp)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(1.5.dp, Color.Black, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.White.copy(alpha=0.6f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (currentLanguage) { LanguageEnum.HINDI -> "$participantCount प्रार्थना कर रहे हैं"; LanguageEnum.MALAYALAM -> "$participantCount പ്രാർത്ഥിക്കുന്നു"; else -> "$participantCount Praying" },
                                    fontSize = 12.sp,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Live Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = liveCardProgress.value
                        scaleX = 0.95f + (0.05f * liveCardProgress.value)
                        scaleY = 0.95f + (0.05f * liveCardProgress.value)
                    },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF070B11),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(if (isSessionLive) Color(0xFF8B1A1A) else Color(0xFF1E283A), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSessionLive) Color.White else Gold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSessionLive) {
                                    when (currentLanguage) { LanguageEnum.HINDI -> "लाइव अभी"; LanguageEnum.MALAYALAM -> "ഇപ്പോൾ തത്സമയം"; else -> "LIVE NOW" }
                                } else {
                                    when (currentLanguage) { LanguageEnum.HINDI -> "अगला सत्र"; LanguageEnum.MALAYALAM -> "അടുത്ത സെഷൻ"; else -> "NEXT SESSION" }
                                },
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (showPrayingCount) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = "Participants",
                                    tint = Color(0xFF6B9BFF),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) { LanguageEnum.HINDI -> "$participantCount प्रार्थना कर रहे हैं"; LanguageEnum.MALAYALAM -> "$participantCount പ്രാർത്ഥിക്കുന്നു"; else -> "$participantCount Praying" },
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B9BFF),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Icon 
                        Image(
                            painter = painterResource(id = R.drawable.mary_glowing_icon_1786301877465),
                            contentDescription = "Mary Icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Right Text
                        Column {
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "वर्तमान भेद"; LanguageEnum.MALAYALAM -> "ഇപ്പോഴത്തെ രഹസ്യം"; else -> "Current Mystery" },
                                fontSize = 11.sp,
                                color = Color(0xFFFFB300),
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> RosaryPrayers.getMysteriesForType(roomState.currentMysteryType).first().hindiTitle
                                    LanguageEnum.MALAYALAM -> RosaryPrayers.getMysteriesForType(roomState.currentMysteryType).first().malayalamTitle
                                    else -> RosaryPrayers.getMysteriesForType(roomState.currentMysteryType).first().englishTitle
                                },
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> roomState.currentMysteryType.hindiTitle; LanguageEnum.MALAYALAM -> roomState.currentMysteryType.malayalamTitle; else -> roomState.currentMysteryType.englishTitle },
                                fontSize = 12.sp,
                                color = Color(0xFFFFB300),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "\"हमारे साथ प्रार्थना करें और एकजुटता की शक्ति का अनुभव करें।\""
                                    LanguageEnum.MALAYALAM -> "\"ഞങ്ങളോടൊപ്പം പ്രാർത്ഥിച്ച് ഐക്യത്തിന്റെ ശക്തി അനുഭവിക്കുക.\""
                                    else -> "\"Pray with us and experience\nthe power of togetherness.\""
                                },
                                fontSize = 11.sp,
                                color = Color(0xFFD1D5DB),
                                lineHeight = 14.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onJoinLiveRoom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = if (isSessionLive) listOf(
                                        Color(0xFFFFD966),
                                        Color(0xFFD49E2D)
                                    ) else listOf(
                                        Color(0xFF3B4A6B),
                                        Color(0xFF1E283A)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = if (isSessionLive) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = if (isSessionLive) Icons.Default.PlayCircleFilled else Icons.Default.HourglassTop,
                            contentDescription = "Join Live",
                            modifier = Modifier.size(24.dp),
                            tint = if (isSessionLive) Color.Black else Gold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isSessionLive) {
                                when (currentLanguage) { LanguageEnum.HINDI -> "लाइव रोज़री में शामिल हों"; LanguageEnum.MALAYALAM -> "തത്സമയ ജപമാലയിൽ പങ്കെടുക്കുക"; else -> "JOIN LIVE ROSARY" }
                            } else {
                                when (currentLanguage) { LanguageEnum.HINDI -> "वेटिंग लाउंज में जाएँ"; LanguageEnum.MALAYALAM -> "കാത്തിരിപ്പ് മുറിയിൽ പ്രവേശിക്കുക"; else -> "ENTER WAITING LOUNGE" }
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSessionLive) Color.Black else Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leader Schedule & Slot Claiming Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0D131F),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "👑",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = when (currentLanguage) { LanguageEnum.HINDI -> "प्रार्थना संचालक स्लॉट"; LanguageEnum.MALAYALAM -> "പ്രാർത്ഥന നയിക്കുന്നവരുടെ സമയക്രമം"; else -> "PRAYER LEADER SCHEDULE" },
                                    fontSize = 13.sp,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "आज का स्लॉट बुक करें और प्रार्थना का नेतृत्व करें"; LanguageEnum.MALAYALAM -> "തത്സമയ ജപമാല നയിക്കാൻ ഒരു സമയം തിരഞ്ഞെടുക്കുക"; else -> "Book a slot to lead today's live Rosary" },
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val allSessions = currentSchedule.getAllSessions()
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(allSessions) { session ->
                            val effectiveHost = currentSchedule.getEffectiveHostForSession(session.name)
                            val isClaimed = currentSchedule.isSlotClaimed(session.name)
                            val isMyClaim = isClaimed && savedUserName.isNotBlank() && savedUserName.equals(effectiveHost, ignoreCase = true)
                            val isActiveSession = currentSchedule.getCurrentActiveSession()?.name == session.name

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isActiveSession) Color(0xFF1E283A) else Color(0xFF070B11),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isActiveSession) 1.5.dp else 1.dp,
                                    if (isMyClaim) Color(0xFF64FFDA) else if (isActiveSession) Gold else Color(0x33FFFFFF)
                                ),
                                modifier = Modifier.width(210.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = session.name,
                                            fontSize = 12.sp,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isActiveSession) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF8B1A1A)
                                            ) {
                                                Text(
                                                    text = "LIVE",
                                                    fontSize = 8.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "⏰ ${session.startTime} - ${session.endTime}",
                                        fontSize = 10.sp,
                                        color = Gold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isClaimed) Icons.Default.Star else Icons.Outlined.Person,
                                            contentDescription = null,
                                            tint = if (isMyClaim) Color(0xFF64FFDA) else if (isClaimed) Gold else TextGray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isMyClaim) {
                                                when (currentLanguage) { LanguageEnum.HINDI -> "👑 आप ($effectiveHost)"; LanguageEnum.MALAYALAM -> "👑 നിങ്ങൾ ($effectiveHost)"; else -> "👑 You ($effectiveHost)" }
                                            } else if (isClaimed) {
                                                "👑 $effectiveHost"
                                            } else {
                                                "🎧 $effectiveHost"
                                            },
                                            fontSize = 11.sp,
                                            color = if (isMyClaim) Color(0xFF64FFDA) else if (isClaimed) TextWhite else TextGray,
                                            fontWeight = if (isClaimed) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (isMyClaim) {
                                        OutlinedButton(
                                            onClick = { viewModel.cancelLeaderSlot(session.name) },
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFFFF6B6B)
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFFF6B6B).copy(alpha = 0.6f)),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(when (currentLanguage) { LanguageEnum.HINDI -> "रद्द करें (Cancel)"; LanguageEnum.MALAYALAM -> "റദ്ദാക്കുക"; else -> "Cancel Slot" }, fontSize = 10.sp)
                                        }
                                    } else if (isClaimed) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0x22FFFFFF)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = when (currentLanguage) { LanguageEnum.HINDI -> "आरक्षित (Booked)"; LanguageEnum.MALAYALAM -> "ബുക്ക് ചെയ്തു"; else -> "Booked" },
                                                    fontSize = 10.sp,
                                                    color = TextGray
                                                )
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                leaderNameInput = ""
                                                selectedSessionToBook = session
                                            },
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Gold,
                                                contentColor = Color.Black
                                            ),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = when (currentLanguage) { LanguageEnum.HINDI -> "संचालक बनें"; LanguageEnum.MALAYALAM -> "നേതാവാകുക"; else -> "BECOME LEADER" },
                                                fontSize = 10.sp,
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

            Spacer(modifier = Modifier.height(16.dp))

            // Solo Rosary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .graphicsLayer {
                        alpha = soloCardProgress.value
                        scaleX = 0.95f + (0.05f * soloCardProgress.value)
                        scaleY = 0.95f + (0.05f * soloCardProgress.value)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoloCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoloCardBorder)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.solo_rosary_bg_1786303317708),
                        contentDescription = "Solo Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = 0.7f
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        SoloCardBg,
                                        SoloCardBg.copy(alpha = 0.8f),
                                        Color.Transparent
                                    ),
                                    startX = 0f,
                                    endX = 800f
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .border(1.5.dp, SoloBlue.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(56.dp)) {
                                drawCircle(
                                    color = SoloBlue,
                                    radius = size.minDimension / 2,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 4.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 6.dp.toPx()), 0f)
                                    )
                                )
                                drawLine(
                                    color = SoloBlue,
                                    start = Offset(size.width / 2, size.height),
                                    end = Offset(size.width / 2, size.height + 12.dp.toPx()),
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawLine(
                                    color = SoloBlue,
                                    start = Offset(size.width / 2 - 6.dp.toPx(), size.height + 6.dp.toPx()),
                                    end = Offset(size.width / 2 + 6.dp.toPx(), size.height + 6.dp.toPx()),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(20.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "व्यक्तिगत रोज़री"; LanguageEnum.MALAYALAM -> "വ്യക്തിഗത ജപമാല"; else -> "SOLO ROSARY" },
                                fontSize = 16.sp,
                                color = SoloBlue,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "अपनी गति से प्रार्थना करें"; LanguageEnum.MALAYALAM -> "നിങ്ങളുടെ സൗകര്യമനുസരിച്ച് പ്രാർത്ഥിക്കുക"; else -> "Pray at your own pace" },
                                fontSize = 14.sp,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = onStartSoloPrayer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SoloCardBorder),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = SoloButtonBg,
                                    contentColor = SoloBlue
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier.heightIn(min = 36.dp)
                            ) {
                                Text(
                                    text = when (currentLanguage) { LanguageEnum.HINDI -> "शुरू करें"; LanguageEnum.MALAYALAM -> "സ്റ്റാർട്ട് സോളോ റോസറി"; else -> "START SOLO ROSARY" },
                                    fontSize = if (currentLanguage == LanguageEnum.MALAYALAM) 9.sp else 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Start",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Watch Devotional Videos Card (Live or Video Library)
            Surface(
                onClick = onOpenWatchVideos,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isAnyVideoLive) Color(0xFF14080A) else Color(0xFF060D1A),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAnyVideoLive) Color(0xFFE50914).copy(alpha = 0.7f) else Color(0xFF1E293B)
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (isAnyVideoLive) Color(0xFFE50914).copy(alpha = 0.15f)
                                else Color(0xFFD4AF37).copy(alpha = 0.12f)
                            )
                            .border(
                                1.5.dp,
                                if (isAnyVideoLive) Color(0xFFE50914).copy(alpha = 0.6f)
                                else Color(0xFFD4AF37).copy(alpha = 0.4f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAnyVideoLive) Icons.Default.LiveTv else Icons.Default.PlayCircleFilled,
                            contentDescription = "Watch Videos",
                            tint = if (isAnyVideoLive) Color(0xFFE50914) else Gold,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isAnyVideoLive) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE50914)
                                ) {
                                    Text(
                                        text = "🔴 LIVE STREAM",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0F172A),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.VideoLibrary,
                                            contentDescription = null,
                                            tint = Gold,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "VIDEO LIBRARY",
                                            color = Gold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Text(
                                text = "YOUTUBE",
                                fontSize = 11.sp,
                                color = if (isAnyVideoLive) Gold else TextGray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isAnyVideoLive) {
                                when (currentLanguage) {
                                    LanguageEnum.HINDI -> "लाइव दर्शन व प्रसारण"
                                    LanguageEnum.MALAYALAM -> "തത്സമയ ദർശനം"
                                    else -> "Live Stream Broadcast"
                                }
                            } else {
                                when (currentLanguage) {
                                    LanguageEnum.HINDI -> "भक्ति वीडियो व भजन संग्रह"
                                    LanguageEnum.MALAYALAM -> "ഭക്തി വീഡിയോകളും ഗാനങ്ങളും"
                                    else -> "Devotional Videos & Hymns"
                                }
                            },
                            fontSize = 15.sp,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "पवित्र मिस्सा, रोज़री माला, सुसमाचार व मसीही भजन"
                                LanguageEnum.MALAYALAM -> "വിശുദ്ധ കുർബാന, ജപമാല, വചനസന്ദേശം, ഭക്തിഗാനങ്ങൾ"
                                else -> "Holy Mass, Rosary, Gospel Teachings & Hymns"
                            },
                            fontSize = 11.sp,
                            color = TextGray,
                            lineHeight = 15.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = Gold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Mystery Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MysteryCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, MysteryCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "आज के भेद"; LanguageEnum.MALAYALAM -> "ഇന്നത്തെ രഹസ്യം"; else -> "TODAY'S MYSTERY" },
                                fontSize = 11.sp,
                                color = MysteryGreen,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> todayDefaultMystery.hindiTitle; LanguageEnum.MALAYALAM -> todayDefaultMystery.malayalamTitle; else -> todayDefaultMystery.englishTitle },
                                fontSize = 20.sp,
                                color = TextWhite,
                                fontFamily = FontFamily.Serif
                            )
                        }
                        OutlinedButton(
                            onClick = onOpenNovena,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MysteryCardBorder),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MysteryButtonBg,
                                contentColor = MysteryGreen
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.heightIn(min = 32.dp)
                        ) {
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "सभी देखें"; LanguageEnum.MALAYALAM -> "എല്ലാം കാണുക"; else -> "VIEW ALL" },
                                fontSize = if (currentLanguage == LanguageEnum.MALAYALAM) 9.sp else 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View All",
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                        // Connecting dotted line
                        Canvas(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(top = 22.dp)) {
                            drawLine(
                                color = GoldDim.copy(alpha = 0.3f),
                                start = Offset(40.dp.toPx(), 0f),
                                end = Offset(size.width - 40.dp.toPx(), 0f),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 8.dp.toPx()), 0f)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val icons = listOf(Icons.Outlined.Face, Icons.Default.People, Icons.Default.StarBorder, Icons.Outlined.FavoriteBorder, Icons.Default.VolunteerActivism)
                            mysteries.forEachIndexed { index, mystery ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(MysteryCardBg)
                                                .border(1.5.dp, Gold.copy(alpha = 0.8f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icons[index % icons.size],
                                                contentDescription = "Mystery Icon",
                                                tint = Gold,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Text(
                                            text = "${mystery.index}",
                                            color = TextWhite,
                                            fontSize = 10.sp,
                                            modifier = Modifier.align(Alignment.TopStart).padding(start=2.dp, top=0.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = when (currentLanguage) { LanguageEnum.HINDI -> mystery.hindiTitle; LanguageEnum.MALAYALAM -> mystery.malayalamTitle; else -> mystery.englishTitle },
                                        color = TextWhite,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 14.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Prayer Intentions
            Surface(
                onClick = onOpenNovena,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = IntentionCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, IntentionCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .border(1.5.dp, IntentionCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "Intentions",
                            tint = IntentionPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "प्रार्थना के इरादे"; LanguageEnum.MALAYALAM -> "പ്രാർത്ഥനാ നിയോഗങ്ങൾ"; else -> "PRAYER INTENTIONS" },
                            fontSize = 13.sp,
                            color = IntentionPurple,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "दूसरों के लिए प्रार्थना करें और उनकी जरूरतों को परमेश्वर के सामने लाएं।"; LanguageEnum.MALAYALAM -> "മറ്റുള്ളവർക്കുവേണ്ടി പ്രാർത്ഥിക്കുക, അവരുടെ ആവശ്യങ്ങൾ ദൈവസമക്ഷം സമർപ്പിക്കുക."; else -> "Pray for others and bring their needs before God." },
                            fontSize = 12.sp,
                            color = TextGray,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onOpenNovena,
                            border = androidx.compose.foundation.BorderStroke(1.dp, IntentionCardBorder),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = IntentionButtonBg,
                                contentColor = IntentionPurple
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.heightIn(min = 36.dp)
                        ) {
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "प्रार्थना जोड़ें"; LanguageEnum.MALAYALAM -> "നിയോഗം ചേർക്കുക"; else -> "ADD INTENTION" },
                                fontSize = if (currentLanguage == LanguageEnum.MALAYALAM) 9.sp else 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Add",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAdminPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminPinDialog = false
                enteredPin = ""
                pinError = false
            },
            title = {
                Text(
                    text = "Admin Access",
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter Admin Password:",
                        color = TextWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = false
                        },
                        singleLine = true,
                        isError = pinError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinError) {
                        Text(
                            text = "Incorrect Password.",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredPin.trim() == "gharanayeshuka") {
                            showAdminPinDialog = false
                            enteredPin = ""
                            pinError = false
                            onOpenAdmin()
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color.Black)
                ) {
                    Text("Submit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAdminPinDialog = false
                        enteredPin = ""
                        pinError = false
                    }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }

    if (selectedSessionToBook != null) {
        val session = selectedSessionToBook!!
        AlertDialog(
            onDismissRequest = { selectedSessionToBook = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑 ", fontSize = 20.sp)
                    Text(
                        text = when (currentLanguage) { LanguageEnum.HINDI -> "रोज़री संचालक पंजीकरण"; LanguageEnum.MALAYALAM -> "ജപമാല നയിക്കുന്നവർക്കുള്ള രജിസ്ട്രേഷൻ"; else -> "Rosary Leader Registration" },
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = if (currentLanguage == LanguageEnum.HINDI)
                            "आप केवल \"${session.name}\" (${session.startTime} - ${session.endTime}) सत्र के लिए संचालक पद बुक कर रहे हैं।"
                        else
                            "You are booking the leader slot ONLY for \"${session.name}\" (${session.startTime} - ${session.endTime}).",
                        color = TextWhite,
                        fontSize = 13.sp,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (currentLanguage) { LanguageEnum.HINDI -> "अपना नाम लिखें:"; LanguageEnum.MALAYALAM -> "നിങ്ങളുടെ പേര് നൽകുക:"; else -> "Enter your name:" },
                        color = TextGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = leaderNameInput,
                        onValueChange = { leaderNameInput = it },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "नाम लिखें"; LanguageEnum.MALAYALAM -> "പേര് നൽകുക"; else -> "Enter Name" },
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nameToSave = leaderNameInput.ifBlank { "Prayer Leader" }
                        viewModel.claimLeaderSlot(session.name, nameToSave)
                        selectedSessionToBook = null
                        Toast.makeText(
                            context,
                            when (currentLanguage) { LanguageEnum.HINDI -> "आप सफलतापूर्वक संचालक बन गए हैं!"; LanguageEnum.MALAYALAM -> "നിങ്ങൾ ഈ സമയത്ത് പ്രാർത്ഥന നയിക്കും!"; else -> "You are now the leader for this slot!" },
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color.Black)
                ) {
                    Text(when (currentLanguage) { LanguageEnum.HINDI -> "पुष्टि करें (Confirm)"; LanguageEnum.MALAYALAM -> "ഉറപ്പാക്കുക"; else -> "Confirm Booking" }, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSessionToBook = null }) {
                    Text(when (currentLanguage) { LanguageEnum.HINDI -> "रद्द करें"; LanguageEnum.MALAYALAM -> "റദ്ദാക്കുക"; else -> "Cancel" }, color = Color.Gray)
                }
            },
            containerColor = Color(0xFF131A26)
        )
    }
}
