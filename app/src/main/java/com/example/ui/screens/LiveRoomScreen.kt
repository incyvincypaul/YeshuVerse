package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageEnum
import com.example.model.MysteryType
import com.example.model.RosaryBeadStep

import com.example.ui.components.RosaryBeadsCanvas
import com.example.ui.components.CandleItem
import com.example.ui.components.DecadeSelector
import com.example.ui.components.MeditationThemeBox
import com.example.ui.components.MysteryHeader
import com.example.ui.theme.SacredBlack
import com.example.ui.theme.SacredBlueDark
import com.example.ui.theme.SacredBlueLight
import com.example.ui.theme.SacredCardBg
import com.example.ui.theme.SacredCardBorder
import com.example.ui.theme.SacredDarkSurface
import com.example.ui.theme.SacredGold
import com.example.ui.theme.StatusLiveRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.RosaryViewModel

object LeaderNames {
    val names = listOf(
        "Father John", "Father Thomas", "Sister Maria", "Father Paul", "Sister Elizabeth",
        "Father Peter", "Sister Theresa", "Father James", "Father Matthew", "Sister Catherine",
        "Father Joseph", "Sister Agnes", "Father Francis", "Father Anthony", "Sister Bernadette",
        "Father Luke", "Sister Claire", "Father Mark", "Father Simon", "Sister Faustina",
        "Father Andrew", "Sister Josephine", "Father Philip", "Father Bartholomew", "Sister Monica",
        "Father Jude", "Sister Rita", "Father Thaddeus", "Father Gregory", "Sister Rose",
        "Father Benedict", "Sister Helen", "Father Ambrose", "Father Augustine", "Sister Cecilia",
        "Father Dominic", "Sister Barbara", "Father Ignatius", "Father Xavier", "Sister Lucia",
        "Father Jerome", "Sister Gianna", "Father Pio", "Father Maximilian", "Sister Veronica",
        "Father Vincent", "Sister Philomena", "Father Patrick", "Father Sebastian", "Sister Gemma"
    )

    fun getDailyLeader(): String {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return names[dayOfYear % names.size]
    }
}

@Composable
fun AudioVisualizerBars(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFFFF9F0A)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(320, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(480, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h4"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val heights = listOf(h1, h2, h3, h4)
        heights.forEach { fraction ->
            val scale = if (isPlaying) fraction else 0.25f
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((14 * scale).dp.coerceAtLeast(3.dp))
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRoomScreen(
    viewModel: RosaryViewModel,
    onBackClick: () -> Unit,
    onAdminClick: () -> Unit = {},
    onStartSoloPrayer: () -> Unit = {}
) {
    val context = LocalContext.current
    val roomState by viewModel.roomState.collectAsState()
    val rosarySequence by viewModel.rosarySequence.collectAsState()
    val isHostMode by viewModel.isHostMode.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val showPrayingCount by viewModel.showPrayingCount.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setLiveSyncEnabled(false)
            viewModel.leaveLivePrayer()
        }
    }

    val schedule by viewModel.schedule.collectAsState()
    val nextUpcomingSession by viewModel.nextUpcomingSession.collectAsState()
    val loopWaitTimeRemaining by viewModel.loopWaitTimeRemaining.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()

    // Completion Dialog state
    val isHindi = currentLanguage == LanguageEnum.HINDI
    val maxStep = rosarySequence.lastIndex
    
    // UI State vars that got deleted
    var selectedTopTab by remember { mutableStateOf(0) }
    
    var showBecomeHostDialog by remember { mutableStateOf(false) }
    var hostNameInput by remember { mutableStateOf("") }
    var hostPasswordInput by remember { mutableStateOf("") }
    var hostError by remember { mutableStateOf(false) }

    // Audio / other stuff
    val audioService by viewModel.audioService.collectAsState()
    val isMuted by audioService?.isMuted?.collectAsState() ?: remember { mutableStateOf(false) }
    var upcomingWaitTimeRemaining by remember { mutableStateOf(0L) }
    LaunchedEffect(nextUpcomingSession, activeSession) {
        while(isActive) {
            if (activeSession == null && nextUpcomingSession != null) {
                val timeStr = nextUpcomingSession!!.startTime
                val parts = timeStr.split(":")
                var hours = 0
                var mins = 0
                if (parts.size >= 2) {
                    hours = parts[0].filter { it.isDigit() }.toIntOrNull() ?: 0
                    mins = parts[1].filter { it.isDigit() }.toIntOrNull() ?: 0
                    if (timeStr.contains("PM", ignoreCase = true) && hours < 12) hours += 12
                    if (timeStr.contains("AM", ignoreCase = true) && hours == 12) hours = 0
                }
                
                val now = java.util.Calendar.getInstance()
                val currentHour = now.get(java.util.Calendar.HOUR_OF_DAY)
                val currentMin = now.get(java.util.Calendar.MINUTE)
                val currentSec = now.get(java.util.Calendar.SECOND)
                
                var targetSeconds = hours * 3600 + mins * 60
                val currentSeconds = currentHour * 3600 + currentMin * 60 + currentSec
                
                var diff = targetSeconds - currentSeconds
                if (diff < 0) diff += 86400 // Next day
                
                upcomingWaitTimeRemaining = diff.toLong()
            } else {
                upcomingWaitTimeRemaining = 0L
            }
            delay(1000)
        }
    }

    val showWaitPopup = loopWaitTimeRemaining > 0 || (upcomingWaitTimeRemaining > 0 && upcomingWaitTimeRemaining <= 300)
    if (showWaitPopup) {
        val secondsLeft = if (loopWaitTimeRemaining > 0) loopWaitTimeRemaining.toLong() else upcomingWaitTimeRemaining
        val mins = secondsLeft / 60
        val secs = secondsLeft % 60
        val timeString = String.format("%02d:%02d", mins, secs)
        
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = when (currentLanguage) {
                        LanguageEnum.HINDI -> "⏳ कृपया प्रतीक्षा करें"
                        LanguageEnum.MALAYALAM -> "⏳ ദയവായി കാത്തിരിക്കുക"
                        else -> "⏳ Please Wait"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = when (currentLanguage) {
                        LanguageEnum.HINDI ->
                            "पवित्र रोज़री कुछ ही समय में शुरू होने वाली है।\n\nशुरू होने में समय: $timeString"
                        LanguageEnum.MALAYALAM ->
                            "പരിശുദ്ധ ജപമാല ഉടൻ ആരംഭിക്കും.\n\nആരംഭിക്കാൻ ബാക്കിയുള്ള സമയം: $timeString"
                        else ->
                            "The Holy Rosary is about to start shortly.\n\nStarting in: $timeString"
                    },
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = { },
            containerColor = Color(0xFF1E2235),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFFF9F0A)
        )
    }

    val currentLeader = remember { LeaderNames.getDailyLeader() }
    val isLiveSyncEnabled by viewModel.isLiveSyncEnabled.collectAsState()
    val isLocallyPaused by viewModel.isLocallyPaused.collectAsState()
    var isJoined by remember { mutableStateOf(isLiveSyncEnabled && !isLocallyPaused) }
    var isConnecting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val litCandlesCount by viewModel.litCandlesCount.collectAsState()
    
    var activeToast: android.widget.Toast? by remember { mutableStateOf(null) }

    var connectingSecondsLeft by remember { mutableIntStateOf(10) }
    var isConnectingOverlayVisible by remember { mutableStateOf(true) }

    LaunchedEffect(isLiveSyncEnabled, isLocallyPaused) {
        isJoined = isLiveSyncEnabled && !isLocallyPaused
    }

    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        connectingSecondsLeft = 10
        isConnectingOverlayVisible = true
        while (connectingSecondsLeft > 0) {
            delay(1000L)
            connectingSecondsLeft--
        }
        isConnectingOverlayVisible = false
        viewModel.syncLiveWithScheduleNow()
        if (!isJoined && !isLocallyPaused) {
            viewModel.joinLivePrayer()
        }
    }

    if (isConnectingOverlayVisible && !showBecomeHostDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFFF9F0A),
                        strokeWidth = 2.5.dp
                    )
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "नेटवर्क जोड़ा जा रहा है..."
                            LanguageEnum.MALAYALAM -> "നെറ്റ്‌വർക്ക് കണക്റ്റ് ചെയ്യുന്നു..."
                            else -> "Connecting Network..."
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI ->
                                "नेटवर्क जोड़ा जा रहा है, जल्द ही आप लाइव रोज़री में शामिल हो सकेंगे।"
                            LanguageEnum.MALAYALAM ->
                                "നെറ്റ്‌വർക്ക് കണക്റ്റ് ചെയ്യുന്നു, ഉടൻ തന്നെ നിങ്ങൾക്ക് തത്സമയ ജപമാലയിൽ പങ്കുചേരാം."
                            else ->
                                "Connecting to network, you will join the live rosary shortly."
                        },
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "कनेक्ट हो रहा है: ${connectingSecondsLeft}s"
                            LanguageEnum.MALAYALAM -> "കണക്റ്റ് ചെയ്യുന്നു: ${connectingSecondsLeft}s"
                            else -> "Connecting in: ${connectingSecondsLeft}s"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFF9F0A),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = { },
            containerColor = Color(0xFF1E2235),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    LaunchedEffect(schedule, roomState.isLive, roomState.currentStepIndex) {
        while (isActive) {
            val schedElapsed = schedule?.getElapsedSecondsForActiveSession()
            if (roomState.isLive && schedElapsed != null) {
                elapsedSeconds = schedElapsed
            } else if (roomState.currentStepIndex > 0) {
                elapsedSeconds = (roomState.currentStepIndex * 22).coerceAtLeast(0)
            } else {
                elapsedSeconds = 0
            }
            delay(1000L)
        }
    }

    val elapsedHours = elapsedSeconds / 3600
    val elapsedMinutes = (elapsedSeconds % 3600) / 60
    val remainingSecs = elapsedSeconds % 60
    val elapsedFormatted = if (elapsedHours > 0) {
        String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, remainingSecs)
    } else {
        String.format("%02d:%02d", elapsedMinutes, remainingSecs)
    }

    // Shared candle transition for all animated prayer candles
    val candleTransition = rememberInfiniteTransition(label = "shared_candles")
    val sharedFlameScaleY by candleTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sharedFlameScaleY"
    )
    val sharedFlameScaleX by candleTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sharedFlameScaleX"
    )
    val sharedFlameAlpha by candleTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sharedFlameAlpha"
    )
    val sharedAuraScale by candleTransition.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sharedAuraScale"
    )
    val sharedFlameSwayX by candleTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sharedFlameSwayX"
    )

    val currentStep = rosarySequence.getOrNull(roomState.currentStepIndex)
    val currentDecade = currentStep?.decadeIndex ?: 1

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YESHUVERSE",
                            style = MaterialTheme.typography.titleMedium,
                            color = SacredGold,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (roomState.isLive) StatusLiveRed else TextSecondary)
                        )
                        if (showPrayingCount) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${viewModel.participantCount.collectAsState().value} Praying",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    if (!isHostMode) {
                        IconButton(
                            onClick = {
                                showBecomeHostDialog = true
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Become Host",
                                tint = SacredGold
                            )
                        }
                    }

                    // English / Hindi Switch Button (Only available before live prayer starts)
                    if (!isJoined && !isLiveSyncEnabled && !roomState.isLive) {
                        Surface(
                            onClick = {
                                val nextLang = when (currentLanguage) { LanguageEnum.ENGLISH -> LanguageEnum.HINDI; LanguageEnum.HINDI -> LanguageEnum.MALAYALAM; else -> LanguageEnum.ENGLISH }
                                viewModel.setLanguage(nextLang)
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = SacredBlueDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SacredBlueLight),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = SacredGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentLanguage.nativeName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SacredBlack,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = SacredBlack
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (isConnectingOverlayVisible || !roomState.isLive) 28.dp else 0.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 240.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // Display Broadcast Message if present
            if (!schedule?.broadcastMessage.isNullOrEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SacredBlueDark.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = SacredGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        val msg = if (currentLanguage == LanguageEnum.HINDI && schedule?.broadcastMessage == "Welcome to YeshuVerse Live Rosary") {
                            "येशुवर्स लाइव रोज़री में आपका स्वागत है... वैश्विक प्रार्थना में हमारे साथ जुड़ें।"
                        } else if (schedule?.broadcastMessage == "Welcome to YeshuVerse Live Rosary") {
                            "Welcome to YeshuVerse Live Rosary... Join us in global continuous prayer."
                        } else {
                            schedule?.broadcastMessage ?: ""
                        }

                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
            }

            if (!roomState.isLive) {
                // Non-Live Scheduled Banner Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = SacredDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> if (nextUpcomingSession != null) "अगली लाइव रोज़री निर्धारित समय पर शुरू होगी" else "आज की सभी लाइव रोज़री समाप्त हो चुकी हैं।"
                                LanguageEnum.MALAYALAM -> if (nextUpcomingSession != null) "അടുത്ത തത്സമയ ജപമാല നിശ്ചിത സമയത്ത് ആരംഭിക്കും" else "ഇന്നത്തെ എല്ലാ തത്സമയ ജപമാലകളും അവസാനിച്ചു."
                                else -> if (nextUpcomingSession != null) "NEXT LIVE ROSARY IS SCHEDULED" else "All today's Live Rosary sessions have ended."
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = SacredGold,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> if (nextUpcomingSession != null) "लाइव प्रार्थना के निर्धारित समय पर लाइव रूम चालू हो जाएगा।" else "कृपया कल के निर्धारित समय पर पुनः जुड़ें।"
                                LanguageEnum.MALAYALAM -> if (nextUpcomingSession != null) "തത്സമയ ജപമാല ഷെഡ്യൂൾ പ്രകാരം ആരംഭിക്കും." else "നാളെ വീണ്ടും ഞങ്ങളോടൊപ്പം ചേരുക."
                                else -> if (nextUpcomingSession != null) "Live Rosary starts according to the daily schedule." else "Please join us again tomorrow."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        if (nextUpcomingSession != null) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = SacredCardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (currentLanguage) { LanguageEnum.HINDI -> "अगली आगामी लाइव रोज़री :"; LanguageEnum.MALAYALAM -> "അടുത്ത തത്സമയ ജപമാല :"; else -> "NEXT UPCOMING LIVE ROSARY :" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SacredGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${nextUpcomingSession?.name} (${nextUpcomingSession?.startTime} - ${nextUpcomingSession?.endTime})",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 1. Top Prominent Buttons: Live Community vs Private Prayers

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Live Community Button
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF9F0A))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "लाइव कम्युनिटी रोज़री"; LanguageEnum.MALAYALAM -> "തത്സമയ സമൂഹ ജപമാല"; else -> "Live Community Rosary" },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }

                // Shared Prayer Status pill & Invite Share Action
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF131722))
                        .border(1.dp, Color(0xFF232A3E), RoundedCornerShape(12.dp))
                        .clickable {
                            val shareMessage = when (currentLanguage) { LanguageEnum.HINDI -> "आइए साथ मिलकर लाइव पवित्र रोज़री (माला) प्रार्थना करें! 🕯️✨\n\nयेशुवर्स (YeshuVerse) ऐप पर लाइव प्रार्थना में जुड़ने के लिए क्लिक करें:\nhttps://yeshuverse.app/join?room=live_rosary"; LanguageEnum.MALAYALAM -> "Join me in praying the Live Holy Rosary together! 🕯️✨\n\nClick to join live prayer on YeshuVerse:\nhttps://yeshuverse.app/join?room=live_rosary"; else -> "Join me in praying the Live Holy Rosary together! 🕯️✨\n\nClick to join live prayer on YeshuVerse:\nhttps://yeshuverse.app/join?room=live_rosary" }
                            
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(
                                sendIntent, 
                                when (currentLanguage) { LanguageEnum.HINDI -> "प्रार्थना का लिंक शेयर करें"; LanguageEnum.MALAYALAM -> "പ്രാർത്ഥന ലിങ്ക് ഷെയർ ചെയ്യുക"; else -> "Share Live Prayer Link" }
                            )
                            context.startActivity(shareIntent)
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Prayer Link",
                            tint = Color(0xFFFF9F0A),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "साझा प्रार्थना • शेयर 🔗"; LanguageEnum.MALAYALAM -> "പ്രാർത്ഥന ഷെയർ ചെയ്യുക 🔗"; else -> "Shared Prayer • Share 🔗" },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }
            }

            // 3. Devotion / Altar Main Card with Header & Candle Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF12141D),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232A3E))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Main Titles
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (selectedTopTab == 0) {
                                when (currentLanguage) { LanguageEnum.HINDI -> "कम्युनिटी वेदी रोज़री 🔥"; LanguageEnum.MALAYALAM -> "Community Altar Rosary 🔥"; else -> "Community Altar Rosary 🔥" }
                            } else {
                                when (currentLanguage) { LanguageEnum.HINDI -> "व्यक्तिगत पवित्र रोज़री 🔥"; LanguageEnum.MALAYALAM -> "വ്യക്തിഗത പരിശുദ്ധ ജപമാല 🔥"; else -> "Personal Holy Rosary 🔥" }
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedTopTab == 0) {
                                when (currentLanguage) { LanguageEnum.HINDI -> "यह समस्त समुदाय की साझा प्रार्थना है। यहाँ आपके द्वारा जलाई गई मोमबत्तियाँ सभी को दिखाई देती हैं।"; LanguageEnum.MALAYALAM -> "This is a shared community prayer sanctuary. Candles lit here are visible to all members."; else -> "This is a shared community prayer sanctuary. Candles lit here are visible to all members." }
                            } else {
                                when (currentLanguage) { LanguageEnum.HINDI -> "यह आपकी व्यक्तिगत भक्ति साधना है। आप अपनी सुविधानुसार माला पूरी कर सकते हैं।"; LanguageEnum.MALAYALAM -> "This is your personal devotional sanctuary. Complete your Rosary at your convenience."; else -> "This is your personal devotional sanctuary. Complete your Rosary at your convenience." }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                    }
                }
            }

            // 4. Active Rosary prayer card (Shown ONLY during live sessions) vs Solo Prayer Lounge (Shown when non-live)
            if (roomState.isLive || activeSession != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = SacredCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Mystery Dropdown Header
                        MysteryHeader(
                            currentMystery = roomState.currentMysteryType,
                            onMysteryChange = { mystery ->
                                if (isHostMode || !isLiveSyncEnabled) {
                                    viewModel.changeMystery(mystery)
                                }
                            },
                            language = currentLanguage,
                            isMuted = isMuted,
                            onToggleMute = { audioService?.toggleMute() }
                        )

                        // Decade Circle Badges row (Decade 1..5) - purely automatic indicator in Live Room
                        DecadeSelector(
                            selectedDecade = currentDecade.coerceIn(1, 5),
                            language = currentLanguage,
                            trailingContent = {
                                if (activeSession != null) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFF1B2333),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9F0A))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = Color(0xFFFF9F0A),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = when (currentLanguage) { LanguageEnum.HINDI -> "${elapsedFormatted} निकल गए"; LanguageEnum.MALAYALAM -> "${elapsedFormatted} elapsed"; else -> "${elapsedFormatted} elapsed" },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFFF9F0A),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Meditation Theme display box
                        MeditationThemeBox(
                            currentStep = currentStep,
                            currentMystery = roomState.currentMysteryType,
                            language = currentLanguage
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Stunning decade ring with clock-aligned beads
                        RosaryBeadsCanvas(
                            steps = rosarySequence,
                            currentStepIndex = roomState.currentStepIndex,
                            onBeadClick = { index ->
                                if (isHostMode || !isLiveSyncEnabled) {
                                    viewModel.jumpToStep(index)
                                }
                            },
                            language = currentLanguage
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Live Rosary Small Candles Row (Replaces manual Next/Prev buttons)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (litCandlesCount < 4) {
                                        viewModel.lightCandle()
                                        activeToast?.cancel()
                                        val msg = when (currentLanguage) { LanguageEnum.HINDI -> "आपने प्रार्थना मोमबत्ती जलाई! 🕯️"; LanguageEnum.MALAYALAM -> "നിങ്ങൾ ഒരു പ്രാർത്ഥനാ മെഴുകുതിരി കത്തിച്ചു! 🕯️"; else -> "You lit a prayer candle! 🕯️" }
                                        activeToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                                        activeToast?.show()
                                    }
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0F1118),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2330))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left small candles
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    for (i in 1..2) {
                                        val isLit = i <= litCandlesCount
                                        Box(
                                            modifier = Modifier.graphicsLayer {
                                                scaleX = 0.65f
                                                scaleY = 0.65f
                                            }
                                        ) {
                                            CandleItem(
                                                isLit = isLit,
                                                flameScaleY = sharedFlameScaleY,
                                                flameScaleX = sharedFlameScaleX,
                                                flameAlpha = sharedFlameAlpha,
                                                auraScale = sharedAuraScale,
                                                flameSwayX = sharedFlameSwayX
                                            )
                                        }
                                    }
                                }

                                // Center Sync Status Label
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        AudioVisualizerBars(
                                            isPlaying = roomState.isPlaying && isJoined,
                                            barColor = Color(0xFFFF9F0A)
                                        )
                                        Text(
                                            text = when (currentLanguage) {
                                                LanguageEnum.HINDI -> if (isHostMode) "👑 आप संचालक हैं" else "🎧 होस्ट: ${if (roomState.hostName.isBlank() || roomState.hostName.equals("Available", ignoreCase = true)) "उपलब्ध (Available)" else roomState.hostName}"
                                                LanguageEnum.MALAYALAM -> if (isHostMode) "👑 നിങ്ങൾ നയിക്കുന്നു" else "🎧 ഹോസ്റ്റ്: ${if (roomState.hostName.isBlank() || roomState.hostName.equals("Available", ignoreCase = true)) "ലഭ്യമാണ്" else roomState.hostName}"
                                                else -> if (isHostMode) "👑 You are Leader" else "🎧 Host: ${if (roomState.hostName.isBlank() || roomState.hostName.equals("Available", ignoreCase = true)) "Available" else roomState.hostName}"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFF9F0A),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isHostMode) {
                                            when (currentLanguage) { LanguageEnum.HINDI -> "आप प्रार्थना का संचालन कर रहे हैं 👑"; LanguageEnum.MALAYALAM -> "നിങ്ങൾ പ്രാർത്ഥന നയിക്കുന്നു 👑"; else -> "You are leading 👑" }
                                        } else {
                                            when (currentLanguage) { LanguageEnum.HINDI -> "$currentLeader प्रार्थना का संचालन कर रहे हैं 🎙️"; LanguageEnum.MALAYALAM -> "$currentLeader പ്രാർത്ഥന നയിക്കുന്നു 🎙️"; else -> "$currentLeader is leading 🎙️" }
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }

                                // Right small candles
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    for (i in 3..4) {
                                        val isLit = i <= litCandlesCount
                                        Box(
                                            modifier = Modifier.graphicsLayer {
                                                scaleX = 0.65f
                                                scaleY = 0.65f
                                            }
                                        ) {
                                            CandleItem(
                                                isLit = isLit,
                                                flameScaleY = sharedFlameScaleY,
                                                flameScaleX = sharedFlameScaleX,
                                                flameAlpha = sharedFlameAlpha,
                                                auraScale = sharedAuraScale,
                                                flameSwayX = sharedFlameSwayX
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Non-Live Solo Prayer Invitation Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = SacredCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "व्यक्तिगत (सोलो) रोज़री प्रार्थना करें ✝️"; LanguageEnum.MALAYALAM -> "വ്യക്തിഗതമായി ജപമാല ചൊല്ലുക ✝️"; else -> "PRAY SOLO ROSARY PRIVATELY ✝️" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "लाइव प्रार्थना सत्र अभी सक्रिय नहीं है। आप अपनी सुविधानुसार व्यक्तिगत रूप से पवित्र माला (रोज़री) शुरू कर सकते हैं।"; LanguageEnum.MALAYALAM -> "Live Rosary prayer is not active at this time. You can pray the Holy Rosary individually at your own pace."; else -> "Live Rosary prayer is not active at this time. You can pray the Holy Rosary individually at your own pace." },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onStartSoloPrayer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SacredGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> "सोलो प्रार्थना शुरू करें"; LanguageEnum.MALAYALAM -> "വ്യക്തിഗത പ്രാർത്ഥന തുടങ്ങുക"; else -> "START SOLO PRAYER" },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
        
        if (roomState.isLive && !isConnectingOverlayVisible) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) { LiveChatBox(modifier = Modifier.padding(bottom = 60.dp)) }
        }

        if (isConnectingOverlayVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
            )
        }

        // Full-screen Popup Modal when Live Rosary is ended / non-live
        if (!roomState.isLive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF07090E).copy(alpha = 0.95f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = SacredCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SacredGold),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Glowing Header Icon
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(SacredGold.copy(alpha = 0.15f))
                                .border(1.dp, SacredGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = SacredGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Popup Title
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "लाइव रोज़री समाप्त हो चुकी है"; LanguageEnum.MALAYALAM -> "തത്സമയ ജപമാല അവസാനിച്ചു"; else -> "LIVE ROSARY HAS ENDED" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SacredGold,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )

                        // Popup Description Message
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> if (nextUpcomingSession != null) "अभी कोई लाइव रोज़री सत्र सक्रिय नहीं है। आप सोलो (व्यक्तिगत) प्रार्थना कर सकते हैं या अगली आगामी लाइव रोज़री के समय जुड़ सकते हैं।" else "आज के सभी निर्धारित लाइव रोज़री सत्र समाप्त हो चुके हैं। कृपया कल के निर्धारित समय पर जुड़े।"
                                LanguageEnum.MALAYALAM -> if (nextUpcomingSession != null) "ഇപ്പോൾ തത്സമയ ജപമാല ലഭ്യമല്ല. നിങ്ങൾക്ക് വ്യക്തിഗതമായി പ്രാർത്ഥിക്കാം അല്ലെങ്കിൽ അടുത്ത സെഷനിൽ ചേരാം." else "ഇന്നത്തെ എല്ലാ തത്സമയ ജപമാലകളും അവസാനിച്ചു. ദയവായി നാളെ വീണ്ടും ചേരുക."
                                else -> if (nextUpcomingSession != null) "No live Rosary session is active right now. You can pray solo or join us for the next scheduled session." else "All today's scheduled live Rosary sessions have ended."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        // Next Upcoming Session Card
                        val upcoming = nextUpcomingSession
                        if (upcoming != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF151B28),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (currentLanguage) { LanguageEnum.HINDI -> "अगली आगामी लाइव रोज़री :"; LanguageEnum.MALAYALAM -> "അടുത്ത തത്സമയ ജപമാല :"; else -> "NEXT UPCOMING LIVE ROSARY :" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SacredGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${upcoming.name}\n(${upcoming.startTime} - ${upcoming.endTime})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Popup Action Buttons (Start Solo Rosary vs Go Back Home)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onStartSoloPrayer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SacredGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (currentLanguage) { LanguageEnum.HINDI -> "सोलो प्रार्थना शुरू करें"; LanguageEnum.MALAYALAM -> "വ്യക്തിഗത പ്രാർത്ഥന തുടങ്ങുക"; else -> "START SOLO PRAYER" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E384D)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (currentLanguage) { LanguageEnum.HINDI -> "मुख्य पृष्ठ पर जाएं"; LanguageEnum.MALAYALAM -> "ഹോം സ്ക്രീനിലേക്ക് പോകുക"; else -> "GO TO HOME SCREEN" },
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
    val savedUserName by viewModel.savedUserName.collectAsState()

    if (showBecomeHostDialog) {
        AlertDialog(
            onDismissRequest = {
                showBecomeHostDialog = false
                hostNameInput = ""
                hostPasswordInput = ""
                hostError = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑 ", fontSize = 20.sp)
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "प्रार्थना का संचालन संभालें"
                            LanguageEnum.MALAYALAM -> "പ്രാർത്ഥന നയിക്കുക"
                            else -> "Take Over Prayer Hosting"
                        },
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "अपना नाम लिखें:"
                            LanguageEnum.MALAYALAM -> "നിങ്ങളുടെ പേര് നൽകുക:"
                            else -> "Enter your name:"
                        },
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = hostNameInput,
                        onValueChange = { hostNameInput = it },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "नाम लिखें"
                                    LanguageEnum.MALAYALAM -> "പേര് നൽകുക"
                                    else -> "Enter Name"
                                },
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SacredGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = hostNameInput.trim().ifBlank { savedUserName.ifBlank { "Prayer Leader" } }
                        viewModel.takeHostRole(finalName)
                        showBecomeHostDialog = false
                        hostNameInput = ""
                        hostPasswordInput = ""
                        hostError = false
                        Toast.makeText(
                            context,
                            when (currentLanguage) {
                                LanguageEnum.HINDI -> "आप प्रार्थना के मुख्य संचालक बन गए हैं!"
                                LanguageEnum.MALAYALAM -> "നിങ്ങൾ ഇപ്പോൾ പ്രാർത്ഥന നയിക്കുന്നു!"
                                else -> "You are now the main prayer host!"
                            },
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SacredGold, contentColor = Color.Black)
                ) {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "संचालक बनें"
                            LanguageEnum.MALAYALAM -> "ഹോസ്റ്റ് ആകുക"
                            else -> "Become Host"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBecomeHostDialog = false
                        hostNameInput = ""
                        hostPasswordInput = ""
                        hostError = false
                    }
                ) {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "रद्द करें"
                            LanguageEnum.MALAYALAM -> "റദ്ദാക്കുക"
                            else -> "Cancel"
                        },
                        color = Color.Gray
                    )
                }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }
    }
