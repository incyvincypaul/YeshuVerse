package com.example.ui.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NovenaData
import com.example.data.NovenaDay
import com.example.data.RosaryPrayers
import com.example.model.LanguageEnum
import com.example.model.PrayerType
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovenaScreen(
    currentLanguage: LanguageEnum,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedDayNum by remember { mutableIntStateOf(1) }
    var completedDays by remember {
        mutableStateOf(
            context.getSharedPreferences("novena_prefs", Context.MODE_PRIVATE)
                .getStringSet("completed_days", emptySet())
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()
        )
    }

    var isTtsPlaying by remember { mutableStateOf(false) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    val softPlayer = remember { SoftMeditationPlayer() }

    // Initialize TTS
    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        ttsEngine = tts
        onDispose {
            softPlayer.stop()
            tts?.stop()
            tts?.shutdown()
        }
    }

    fun saveCompletedDays(newSet: Set<Int>) {
        completedDays = newSet
        context.getSharedPreferences("novena_prefs", Context.MODE_PRIVATE)
            .edit()
            .putStringSet("completed_days", newSet.map { it.toString() }.toSet())
            .apply()
    }

    val selectedDay = NovenaData.getDay(selectedDayNum)

    fun speakNovena() {
        val tts = ttsEngine ?: return
        if (isTtsPlaying) {
            softPlayer.stop()
            tts.stop()
            isTtsPlaying = false
            return
        }

        val locale = when (currentLanguage) { LanguageEnum.HINDI -> Locale("hi", "IN"); LanguageEnum.MALAYALAM -> Locale("ml", "IN"); else -> Locale("en", "IN") }
        tts.setLanguage(locale)

        // Queue speech steps cleanly without reading extra header labels like "हे हमारे पिता" or "समापन प्रार्थना"
        val partsBeforeIntention = when (currentLanguage) {
            LanguageEnum.HINDI -> listOf(
                NovenaData.novenaTitleHindi,
                NovenaData.signOfCrossHindi,
                NovenaData.openingPrayerHindi,
                "${selectedDay.hindiTitle}. ${selectedDay.hindiPrayer}",
                NovenaData.specialIntentionHindi
            )
            LanguageEnum.MALAYALAM -> listOf(
                NovenaData.novenaTitleMalayalam,
                NovenaData.signOfCrossMalayalam,
                NovenaData.openingPrayerMalayalam,
                "${selectedDay.malayalamTitle}. ${selectedDay.malayalamPrayer}",
                NovenaData.specialIntentionMalayalam
            )
            else -> listOf(
                NovenaData.novenaTitleEnglish,
                NovenaData.signOfCrossEnglish,
                NovenaData.openingPrayerEnglish,
                "${selectedDay.englishTitle}. ${selectedDay.englishPrayer}",
                NovenaData.specialIntentionEnglish
            )
        }

        val partsAfterIntention = when (currentLanguage) {
            LanguageEnum.HINDI -> listOf(
                RosaryPrayers.getPrayerText(PrayerType.OUR_FATHER, LanguageEnum.HINDI),
                RosaryPrayers.getPrayerText(PrayerType.HAIL_MARY, LanguageEnum.HINDI),
                RosaryPrayers.getPrayerText(PrayerType.GLORY_BE, LanguageEnum.HINDI),
                NovenaData.concludingPrayerHindi
            )
            LanguageEnum.MALAYALAM -> listOf(
                RosaryPrayers.getPrayerText(PrayerType.OUR_FATHER, LanguageEnum.MALAYALAM),
                RosaryPrayers.getPrayerText(PrayerType.HAIL_MARY, LanguageEnum.MALAYALAM),
                RosaryPrayers.getPrayerText(PrayerType.GLORY_BE, LanguageEnum.MALAYALAM),
                NovenaData.concludingPrayerMalayalam
            )
            else -> listOf(
                RosaryPrayers.getPrayerText(PrayerType.OUR_FATHER, LanguageEnum.ENGLISH),
                RosaryPrayers.getPrayerText(PrayerType.HAIL_MARY, LanguageEnum.ENGLISH),
                RosaryPrayers.getPrayerText(PrayerType.GLORY_BE, LanguageEnum.ENGLISH),
                NovenaData.concludingPrayerEnglish
            )
        }

        val lastUtteranceId = "NovenaPart_Final"
        val pauseUtteranceId = "NovenaPause_20Sec_Intention"

        tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isTtsPlaying = true
                if (utteranceId == pauseUtteranceId) {
                    softPlayer.start()
                }
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId == pauseUtteranceId) {
                    softPlayer.stop()
                }
                if (utteranceId == lastUtteranceId) {
                    softPlayer.stop()
                    isTtsPlaying = false
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                softPlayer.stop()
                isTtsPlaying = false
            }
        })

        // 1. Speak initial prayers up to intention announcement
        partsBeforeIntention.forEachIndexed { index, partText ->
            val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts.speak(partText, queueMode, null, "NovenaPart_Pre_$index")
        }

        // 2. Play 20 seconds silence gap for user's personal intention
        tts.playSilentUtterance(20000L, TextToSpeech.QUEUE_ADD, "NovenaPause_20Sec_Intention")

        // 3. Continue speaking main core prayers and concluding prayer
        partsAfterIntention.forEachIndexed { index, partText ->
            val utteranceId = if (index == partsAfterIntention.lastIndex) lastUtteranceId else "NovenaPart_Post_$index"
            tts.speak(partText, TextToSpeech.QUEUE_ADD, null, utteranceId)
        }

        isTtsPlaying = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> NovenaData.novenaTitleHindi; LanguageEnum.MALAYALAM -> NovenaData.novenaTitleMalayalam; else -> NovenaData.novenaTitleEnglish },
                            style = MaterialTheme.typography.titleMedium,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "शांति और आशा की नौ-दिवसीय प्रार्थना"
                                LanguageEnum.MALAYALAM -> "സമാധാനത്തിനും പ്രത്യാശയ്ക്കും വേണ്ടിയുള്ള 9 ദിവസത്തെ നൊവേന"
                                else -> "9-Day Prayer for Peace & Hope"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = SacredBlueLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        softPlayer.stop()
                        ttsEngine?.stop()
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SacredGold
                        )
                    }
                },
                actions = {
                    // Audio Read out button
                    IconButton(
                        onClick = { speakNovena() }
                    ) {
                        Icon(
                            imageVector = if (isTtsPlaying) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Listen Prayer",
                            tint = if (isTtsPlaying) StatusLiveRed else SacredGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SacredBlack)
            )
        },
        containerColor = SacredBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Day selector strip (Days 1 to 9)
            Surface(
                color = SacredDarkSurface,
                border = BorderStroke(0.5.dp, SacredCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "दिन चुनें (Select Day):"
                            LanguageEnum.MALAYALAM -> "ദിവസം തിരഞ്ഞെടുക്കുക:"
                            else -> "Select Novena Day:"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items((1..9).toList()) { day ->
                            val isSelected = (day == selectedDayNum)
                            val isDone = completedDays.contains(day)

                            Surface(
                                onClick = { selectedDayNum = day },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) SacredGold else if (isDone) SacredBlueDark else SacredCardBg,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SacredGoldLight else if (isDone) SacredGold else SacredCardBorder
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Done",
                                            tint = if (isSelected) Color.Black else SacredGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = when (currentLanguage) {
                                            LanguageEnum.HINDI -> "दिन $day"
                                            LanguageEnum.MALAYALAM -> "ദിവസം $day"
                                            else -> "Day $day"
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Main Scrollable Content of Selected Day
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sign of Cross Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SacredCardBg),
                    border = BorderStroke(1.dp, SacredCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "✠ क्रूस का चिन्ह (Sign of the Cross)"
                                LanguageEnum.MALAYALAM -> "✠ കുരിശടയാളം"
                                else -> "✠ Sign of the Cross"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> NovenaData.signOfCrossHindi; LanguageEnum.MALAYALAM -> NovenaData.signOfCrossMalayalam; else -> NovenaData.signOfCrossEnglish },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Opening Prayer Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SacredCardBg),
                    border = BorderStroke(1.dp, SacredCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SacredGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "प्रारंभिक प्रार्थना (Opening Prayer)"
                                    LanguageEnum.MALAYALAM -> "പ്രാരംഭ പ്രാർത്ഥന"
                                    else -> "Opening Prayer"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = SacredGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> NovenaData.openingPrayerHindi; LanguageEnum.MALAYALAM -> NovenaData.openingPrayerMalayalam; else -> NovenaData.openingPrayerEnglish },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Today's Main Theme & Prayer Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SacredDarkSurface),
                    border = BorderStroke(1.5.dp, SacredGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> selectedDay.hindiTitle; LanguageEnum.MALAYALAM -> selectedDay.malayalamTitle; else -> selectedDay.englishTitle },
                            style = MaterialTheme.typography.headlineSmall,
                            color = SacredGold,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> selectedDay.hindiPrayer; LanguageEnum.MALAYALAM -> selectedDay.malayalamPrayer; else -> selectedDay.englishPrayer },
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Special Intention Reflection Box
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SacredBlueDark.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, SacredBlueLight.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Intention",
                            tint = SacredGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "विशेष मनोकामना"
                                    LanguageEnum.MALAYALAM -> "പ്രത്യേക നിയോഗം"
                                    else -> "Personal Intention"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = SacredGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (currentLanguage) { LanguageEnum.HINDI -> NovenaData.specialIntentionHindi; LanguageEnum.MALAYALAM -> NovenaData.specialIntentionMalayalam; else -> NovenaData.specialIntentionEnglish },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Daily Core Prayers Guidance Card (Our Father, Hail Mary, Glory Be)
                CorePrayersExpandableCard(currentLanguage = currentLanguage)

                // Concluding Prayer Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SacredCardBg),
                    border = BorderStroke(1.dp, SacredCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SacredGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "समापन प्रार्थना (Concluding Prayer)"
                                    LanguageEnum.MALAYALAM -> "സമാപന പ്രാർത്ഥന"
                                    else -> "Concluding Prayer"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = SacredGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> NovenaData.concludingPrayerHindi; LanguageEnum.MALAYALAM -> NovenaData.concludingPrayerMalayalam; else -> NovenaData.concludingPrayerEnglish },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Bottom Action Bar: Complete Day Toggle & Next Day Navigation
            Surface(
                color = SacredDarkSurface,
                border = BorderStroke(1.dp, SacredCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mark as Completed Button
                    val isCurrentDone = completedDays.contains(selectedDayNum)
                    Button(
                        onClick = {
                            val newSet = if (isCurrentDone) {
                                completedDays - selectedDayNum
                            } else {
                                completedDays + selectedDayNum
                            }
                            saveCompletedDays(newSet)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrentDone) Color(0xFF2E7D32) else SacredGold,
                            contentColor = if (isCurrentDone) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Complete",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCurrentDone) {
                                when (currentLanguage) {
                                    LanguageEnum.HINDI -> "दिन $selectedDayNum पूर्ण (Completed)"
                                    LanguageEnum.MALAYALAM -> "ദിവസം $selectedDayNum പൂർത്തിയായി"
                                    else -> "Day $selectedDayNum Done"
                                }
                            } else {
                                when (currentLanguage) {
                                    LanguageEnum.HINDI -> "दिन $selectedDayNum पूर्ण करें"
                                    LanguageEnum.MALAYALAM -> "ദിവസം $selectedDayNum പൂർത്തിയാക്കുക"
                                    else -> "Mark Day $selectedDayNum Done"
                                }
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Next Day Navigation Button
                    if (selectedDayNum < 9) {
                        OutlinedButton(
                            onClick = { selectedDayNum++ },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SacredGold),
                            border = BorderStroke(1.dp, SacredGold),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "अगला दिन ➔"
                                    LanguageEnum.MALAYALAM -> "അടുത്ത ദിവസം ➔"
                                    else -> "Next Day ➔"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "नौवेना पूर्ण हुआ! 🙏"
                                LanguageEnum.MALAYALAM -> "നൊവേന പൂർത്തിയായി! 🙏"
                                else -> "Novena Complete! 🙏"
                            },
                            color = SacredGold,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CorePrayersExpandableCard(currentLanguage: LanguageEnum) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SacredCardBg),
        border = BorderStroke(1.dp, SacredGold.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "इसके बाद निम्नलिखित प्रार्थनाएँ करें:"
                            LanguageEnum.MALAYALAM -> "ഇതിനുശേഷം ഈ പ്രാർത്ഥനകൾ ചൊല്ലുക:"
                            else -> "After this, pray the following:"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            LanguageEnum.HINDI -> "1. हे हमारे पिता  •  2. प्रणाम मरियम  •  3. महिма हो"
                            LanguageEnum.MALAYALAM -> "1. സ്വർഗ്ഗസ്ഥനായ പിതാവേ  •  2. നന്മ നിറഞ്ഞ മറിയമേ  •  3. ത്രിത്വസ്തുതി"
                            else -> "1. Our Father  •  2. Hail Mary  •  3. Glory Be"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand Prayers",
                        tint = SacredGold
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = SacredCardBorder)

                    // Our Father
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "१. हे हमारे पिता (Our Father)"
                                LanguageEnum.MALAYALAM -> "1. സ്വർഗ്ഗസ്ഥനായ പിതാവേ (Our Father)"
                                else -> "1. Our Father"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = RosaryPrayers.getPrayerText(PrayerType.OUR_FATHER, currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }

                    // Hail Mary
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "२. प्रणाम मरियम (Hail Mary)"
                                LanguageEnum.MALAYALAM -> "2. നന്മ നിറഞ്ഞ മറിയമേ (Hail Mary)"
                                else -> "2. Hail Mary"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = RosaryPrayers.getPrayerText(PrayerType.HAIL_MARY, currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }

                    // Glory Be
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "३. महिमा हो (Glory Be)"
                                LanguageEnum.MALAYALAM -> "3. ത്രിത്വസ്തുതി (Glory Be)"
                                else -> "3. Glory Be"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = RosaryPrayers.getPrayerText(PrayerType.GLORY_BE, currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

class SoftMeditationPlayer {
    private var audioTrack: AudioTrack? = null

    @Volatile
    private var isPlaying = false
    private var playerThread: Thread? = null

    @Synchronized
    fun start() {
        if (isPlaying) return
        isPlaying = true
        playerThread = Thread {
            try {
                val sampleRate = 44100
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = if (minBufferSize > 0) minBufferSize * 2 else 4096

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .build()

                audioTrack = track
                track.play()

                // Warm ambient swell (C major 7 frequencies: C4, E4, G4, B4)
                val freqs = floatArrayOf(261.63f, 329.63f, 392.00f, 493.88f)
                var sampleIndex = 0L
                val numSamples = 1024
                val buffer = ShortArray(numSamples * 2)

                while (isPlaying) {
                    for (i in 0 until numSamples) {
                        val t = (sampleIndex + i).toDouble() / sampleRate
                        val lfo = 0.5 + 0.5 * Math.sin(2.0 * Math.PI * 0.15 * t)
                        var wave = 0.0
                        for (f in freqs) {
                            wave += Math.sin(2.0 * Math.PI * f.toDouble() * t)
                        }
                        wave = (wave / freqs.size) * lfo * 0.18
                        val valShort = (wave * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        buffer[i * 2] = valShort
                        buffer[i * 2 + 1] = valShort
                    }
                    sampleIndex += numSamples
                    if (isPlaying) {
                        track.write(buffer, 0, buffer.size)
                    }
                }

                track.stop()
                track.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        playerThread?.start()
    }

    @Synchronized
    fun stop() {
        isPlaying = false
        playerThread = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // ignore
        }
        audioTrack = null
    }
}
