package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageEnum
import com.example.ui.theme.SacredBlueGlow
import com.example.ui.theme.SacredBlueLight
import com.example.ui.theme.SacredCardBg
import com.example.ui.theme.SacredCardBorder
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.RosaryViewModel

@Composable
fun AmbientPrayerMusicPlayer(
    viewModel: RosaryViewModel,
    modifier: Modifier = Modifier
) {
    val audioService by viewModel.audioService.collectAsState()
    val isPlaying = audioService?.isPlaying?.collectAsState()?.value ?: false
    val speechRate = audioService?.speechRate?.collectAsState()?.value ?: 0.85f
    val pitch = audioService?.pitch?.collectAsState()?.value ?: 0.90f
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isHostMode by viewModel.isHostMode.collectAsState()
    val isLiveSyncEnabled by viewModel.isLiveSyncEnabled.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rosary_prayer_vocal_player_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SacredCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Equalizer visualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPlaying) Brush.linearGradient(
                                    listOf(SacredGold, SacredBlueLight)
                                ) else Brush.linearGradient(
                                    listOf(SacredCardBorder, SacredCardBorder)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Rosary Vocal Recitation",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "रोज़री प्रार्थना वाचन"; LanguageEnum.MALAYALAM -> "Rosary Spoken Recitation"; else -> "Rosary Spoken Recitation" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "ध्वनि वाचन और मनका गति तालमेल"; LanguageEnum.MALAYALAM -> "Spoken prayers with automatic bead movement"; else -> "Spoken prayers with automatic bead movement" },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Static Icon
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = if (isPlaying) SacredGoldLight else SacredCardBorder,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Sync Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLiveSyncEnabled) {
                    // Play / Pause Button for Solo Mode (Always available when sync is disabled)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPlaying) Brush.horizontalGradient(listOf(SacredGold, SacredGoldLight))
                                else Brush.horizontalGradient(listOf(SacredBlueLight, SacredBlueGlow))
                            )
                            .clickable {
                                if (isPlaying) viewModel.hostPauseRosary() else viewModel.hostStartRosary()
                            }
                            .testTag("vocal_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause Spoken Prayers" else "Play Spoken Prayers",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "व्यक्तिगत प्रार्थना मोड"; LanguageEnum.MALAYALAM -> "Solo Prayer Mode"; else -> "Solo Prayer Mode" },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "अपनी गति से स्वतंत्र रूप से प्रार्थना का वाचन सुनें"; LanguageEnum.MALAYALAM -> "Control local voice playback at your own pace"; else -> "Control local voice playback at your own pace" },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                } else if (isHostMode) {
                    // Play / Pause Button for the Host
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPlaying) Brush.horizontalGradient(listOf(SacredGold, SacredGoldLight))
                                else Brush.horizontalGradient(listOf(SacredBlueLight, SacredBlueGlow))
                            )
                            .clickable {
                                if (isPlaying) viewModel.hostPauseRosary() else viewModel.hostStartRosary()
                            }
                            .testTag("vocal_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause Spoken Prayers" else "Play Spoken Prayers",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "आप प्रार्थना का नेतृत्व कर रहे हैं"; LanguageEnum.MALAYALAM -> "You are leading the prayer"; else -> "You are leading the prayer" },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "वाचन और मनके सभी के फोन में सिंक होंगे"; LanguageEnum.MALAYALAM -> "Speech & beads will sync across everyone's phone"; else -> "Speech & beads will sync across everyone's phone" },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    // Synced indicator for participants
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Synced with host",
                        tint = SacredBlueLight,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isPlaying) {
                                when (currentLanguage) { LanguageEnum.HINDI -> "लाइव प्रार्थना सुन रहे हैं"; LanguageEnum.MALAYALAM -> "Listening to Live Recitation"; else -> "Listening to Live Recitation" }
                            } else {
                                when (currentLanguage) { LanguageEnum.HINDI -> "प्रार्थना नेता की प्रतीक्षा कर रहे हैं"; LanguageEnum.MALAYALAM -> "Waiting for Prayer Leader"; else -> "Waiting for Prayer Leader" }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isPlaying) SacredGoldLight else TextPrimary
                        )
                        Text(
                            text = when (currentLanguage) { LanguageEnum.HINDI -> "आपका ऑडियो होस्ट के मनकों के साथ सिंक है"; LanguageEnum.MALAYALAM -> "Your audio is synced perfectly with the leader's bead"; else -> "Your audio is synced perfectly with the leader's bead" },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
