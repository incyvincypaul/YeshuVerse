package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageEnum
import com.example.model.MysteryType
import com.example.ui.components.DecadeSelector
import com.example.ui.components.MeditationThemeBox
import com.example.ui.components.MysteryHeader
import com.example.ui.theme.SacredBlack
import com.example.ui.theme.SacredBlueDark
import com.example.ui.theme.SacredBlueLight
import com.example.ui.theme.SacredCardBg
import com.example.ui.theme.SacredCardBorder
import com.example.ui.theme.SacredGold
import com.example.ui.theme.StatusLiveRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.RosaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RosaryPrayerContent(
    viewModel: RosaryViewModel,
    onBackClick: () -> Unit,
    isLiveMode: Boolean,
    leaderName: String
) {
    val context = LocalContext.current
    val roomState by viewModel.roomState.collectAsState()
    val rosarySequence by viewModel.rosarySequence.collectAsState()
    val isHostMode by viewModel.isHostMode.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val selectedTopTab by viewModel.selectedTopTab.collectAsState()
    val litCandlesCount by viewModel.litCandlesCount.collectAsState()
    var activeToast by remember { mutableStateOf<Toast?>(null) }
    
    var isJoined by remember { mutableStateOf(false) }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isLiveMode) "YESHUVERSE LIVE" else "YESHUVERSE SOLO",
                            style = MaterialTheme.typography.titleMedium,
                            color = SacredGold,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        val showPrayingCount by viewModel.showPrayingCount.collectAsState()
                        if (isLiveMode && showPrayingCount) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusLiveRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val currentParticipantCount by viewModel.participantCount.collectAsState()
                            Text(
                                text = "$currentParticipantCount",
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusLiveRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    if (isLiveMode) {
                        IconButton(
                            onClick = { viewModel.toggleHostMode() },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Host Mode",
                                tint = if (isHostMode) SacredGold else TextSecondary
                            )
                        }
                    }

                    // English / Hindi Switch Button
                    Surface(
                        onClick = {
                            val nextLang = if (currentLanguage == LanguageEnum.ENGLISH) LanguageEnum.HINDI else LanguageEnum.ENGLISH
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
                                text = when (currentLanguage) { LanguageEnum.ENGLISH -> "हिंदी"; LanguageEnum.HINDI -> "മലയാളം"; else -> "Eng" },
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live / Solo Status Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLiveMode) Color(0xFFFF9F0A) else Color(0xFF131722))
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isLiveMode) Icons.Default.Whatshot else Icons.Default.People,
                        contentDescription = null,
                        tint = if (isLiveMode) Color.Black else Color(0xFFFF9F0A),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLiveMode) (when (currentLanguage) { LanguageEnum.HINDI -> "लाइव कम्युनिटी रोज़री"; LanguageEnum.MALAYALAM -> "തത്സമയ സമൂഹ ജപമാല"; else -> "Live Community Rosary" })
                               else (when (currentLanguage) { LanguageEnum.HINDI -> "व्यक्तिगत प्रार्थना मोड"; LanguageEnum.MALAYALAM -> "Solo Prayer Mode"; else -> "Solo Prayer Mode" }),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLiveMode) Color.Black else TextPrimary
                    )
                }
            }
            
            // ... (Rest of UI components need to be moved here, adjusted to use parameters)
            // Note: I will need to move CandleItem, MysteryHeader, DecadeSelector, MeditationThemeBox, 
            // etc., and fix imports in this file. This will be a lot.
            
            // For now, let's keep the core structure and build upon it.
            Text("Content for $leaderName - Mode: ${if (isLiveMode) "Live" else "Solo"}", color = Color.White)
        }
    }
}
