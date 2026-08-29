package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RosaryPrayers
import com.example.model.LanguageEnum
import com.example.model.PrayerType
import com.example.ui.components.CandleItem
import com.example.ui.components.DecadeSelector
import com.example.ui.components.MysteryHeader
import com.example.ui.components.PrayerCard
import com.example.ui.components.RosaryBeadsCanvas
import com.example.ui.theme.*
import com.example.viewmodel.SoloRosaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoloPrayerScreen(
    viewModel: SoloRosaryViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val rosarySequence by viewModel.rosarySequence.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val litCandlesCount by viewModel.litCandlesCount.collectAsState()
    val audioService by viewModel.audioService.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.prepareSoloSession()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSoloSession()
        }
    }

    val isPlaying = audioService?.isPlaying?.collectAsState()?.value ?: false
    val isMuted = audioService?.isMuted?.collectAsState()?.value ?: false
    val soloStepIndex = audioService?.currentStepIndex?.collectAsState()?.value ?: 0
    val soloMysteryType = audioService?.currentMysteryType?.collectAsState()?.value ?: viewModel.currentMysteryType.collectAsState().value

    var showAllPrayersDialog by remember { mutableStateOf(false) }

    val currentStep = rosarySequence.getOrNull(soloStepIndex)
    val currentDecade = currentStep?.decadeIndex ?: 1

    // Infinite transition for candlelight flame animations
    val transition = rememberInfiniteTransition(label = "soloCandleFlame")
    val flameScaleY by transition.animateFloat(
        initialValue = 0.9f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "sy"
    )
    val flameScaleX by transition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "sx"
    )
    val flameAlpha by transition.animateFloat(
        initialValue = 0.85f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "fa"
    )
    val auraScale by transition.animateFloat(
        initialValue = 0.92f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "as"
    )
    val flameSwayX by transition.animateFloat(
        initialValue = -1.2f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(750, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "sw"
    )

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = SacredGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "सोलो रोज़री माला"
                                LanguageEnum.MALAYALAM -> "വ്യക്തിഗത ജപമാല"
                                else -> "SOLO PRAYER"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SacredDarkSurface
                )
            )
        },
        containerColor = SacredBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sacred Candle Altar Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.lightCandle() },
                shape = RoundedCornerShape(20.dp),
                color = SacredCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "प्रार्थना मोमबत्तियाँ (टैप करके जलाएं)"
                                LanguageEnum.MALAYALAM -> "പ്രാർത്ഥനാ മെഴുകുതിരികൾ (കത്തിക്കാൻ ടാപ്പ് ചെയ്യുക)"
                                else -> "Devotional Candles (Tap to Light)"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = TextGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "$litCandlesCount/4 जलाए गए"
                                LanguageEnum.MALAYALAM -> "$litCandlesCount/4 കത്തിച്ചു"
                                else -> "$litCandlesCount/4 Lit"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = SacredGoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        for (i in 0 until 4) {
                            CandleItem(
                                isLit = i < litCandlesCount,
                                flameScaleY = flameScaleY,
                                flameScaleX = flameScaleX,
                                flameAlpha = flameAlpha,
                                auraScale = auraScale,
                                flameSwayX = flameSwayX
                            )
                        }
                    }
                }
            }

            // Mystery Header Selector & Mute Toggle
            MysteryHeader(
                currentMystery = soloMysteryType,
                onMysteryChange = { viewModel.changeMystery(it) },
                language = currentLanguage,
                isMuted = isMuted,
                onToggleMute = { audioService?.toggleMute() }
            )

            // View All Rosary Prayers Quick Button
            Surface(
                onClick = { showAllPrayersDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = SacredCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "All Prayers Guide",
                        tint = SacredGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "संपूर्ण रोज़री प्रार्थना संग्रह (11 Prayers Guide)"
                            LanguageEnum.MALAYALAM -> "സമ്പൂർണ്ണ ജപമാല പ്രാർത്ഥനകൾ (11 Prayers Guide)"
                            else -> "View All 11 Rosary Prayers Guide"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Rosary Overall Progress Card
            val progressPercent = if (rosarySequence.isNotEmpty()) (soloStepIndex + 1).toFloat() / rosarySequence.size else 0f
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SacredCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "प्रार्थना प्रगति: मनका ${soloStepIndex + 1}/60"
                                LanguageEnum.MALAYALAM -> "പുരോഗതി: മണി ${soloStepIndex + 1}/60"
                                else -> "Progress: Bead ${soloStepIndex + 1}/60"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(progressPercent * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SacredGoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SacredGold,
                        trackColor = SacredCardBorder
                    )
                }
            }

            // Decade Selector Pills
            DecadeSelector(
                selectedDecade = currentDecade.coerceIn(1, 5),
                onDecadeSelected = { decade ->
                    viewModel.jumpToDecade(decade)
                },
                language = currentLanguage
            )

            // Interactive Rosary Beads Canvas Wrapped in Devotional Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = SacredCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
            ) {
                Box(
                    modifier = Modifier.padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RosaryBeadsCanvas(
                        steps = rosarySequence,
                        currentStepIndex = soloStepIndex,
                        onBeadClick = { index ->
                            viewModel.jumpToStep(index)
                        },
                        language = currentLanguage,
                        isPlaying = isPlaying,
                        onPlayPauseClick = {
                            if (isPlaying) viewModel.hostPauseRosary() else viewModel.hostStartRosary()
                        }
                    )
                }
            }

            // Devotional Prayer Card with full text & manual navigation
            PrayerCard(
                currentStep = currentStep,
                mysteryType = soloMysteryType,
                language = currentLanguage,
                onPreviousClick = { viewModel.hostPreviousStep() },
                onNextClick = { viewModel.hostNextStep() }
            )
        }
    }

    // All Prayers Reference Guide Dialog
    if (showAllPrayersDialog) {
        AllPrayersGuideDialog(
            language = currentLanguage,
            onDismiss = { showAllPrayersDialog = false }
        )
    }
}

@Composable
fun AllPrayersGuideDialog(
    language: LanguageEnum,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val prayerList = remember(language) {
        listOf(
            PrayerType.SIGN_OF_THE_CROSS,
            PrayerType.APOSTLES_CREED,
            PrayerType.OUR_FATHER,
            PrayerType.HAIL_MARY,
            PrayerType.GLORY_BE,
            PrayerType.FATIMA_PRAYER,
            PrayerType.HAIL_HOLY_QUEEN,
            PrayerType.MEMORARE,
            PrayerType.LITANY_OF_LORETO,
            PrayerType.CONCLUDING_PRAYER,
            PrayerType.INTRO_PRAYER
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        containerColor = SacredDarkSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = SacredGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            LanguageEnum.HINDI -> "रोज़री की संपूर्ण प्रार्थनाएँ"
                            LanguageEnum.MALAYALAM -> "സമ്പൂർണ്ണ ജപമാല പ്രാർത്ഥനകൾ"
                            else -> "Complete Rosary Prayers"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                prayerList.forEach { prayerType ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SacredCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = when (language) {
                                    LanguageEnum.HINDI -> prayerType.hindiName
                                    LanguageEnum.MALAYALAM -> prayerType.malayalamName
                                    else -> prayerType.englishName
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = SacredGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = RosaryPrayers.getPrayerText(prayerType, language),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SacredGold)
            ) {
                Text(
                    text = when (language) {
                        LanguageEnum.HINDI -> "बंद करें"
                        LanguageEnum.MALAYALAM -> "അടയ്ക്കുക"
                        else -> "Close"
                    },
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}



