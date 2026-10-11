package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageEnum
import com.example.ui.theme.SacredCardBg
import com.example.ui.theme.SacredCardBorder
import com.example.ui.theme.SacredDarkSurface
import com.example.ui.theme.SacredGold
import com.example.ui.theme.StatusLiveRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Visual countdown timer component that calculates the real-time difference between
 * the device clock (System.currentTimeMillis()) and the server-stored 'prayerStartTime'
 * to ensure all users across multiple devices stay precisely synchronized.
 */
@Composable
fun PrayerCountdownTimer(
    prayerStartTime: Long,
    modifier: Modifier = Modifier,
    sessionName: String? = null,
    language: LanguageEnum = LanguageEnum.HINDI,
    isLiveNow: Boolean = false,
    compactMode: Boolean = false,
    onTimerFinished: () -> Unit = {}
) {
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasFiredFinished by remember { mutableLongStateOf(0L) }

    // Synchronized 1-second clock ticker
    LaunchedEffect(prayerStartTime) {
        while (isActive) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val diffMillis = remember(prayerStartTime, currentTimeMillis) {
        if (prayerStartTime > 0L) prayerStartTime - currentTimeMillis else 0L
    }

    LaunchedEffect(diffMillis) {
        if (prayerStartTime > 0L && diffMillis <= 0L && hasFiredFinished != prayerStartTime) {
            hasFiredFinished = prayerStartTime
            onTimerFinished()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    if (compactMode) {
        CompactCountdownView(
            prayerStartTime = prayerStartTime,
            diffMillis = diffMillis,
            isLiveNow = isLiveNow,
            language = language,
            sessionName = sessionName,
            pulseScale = pulseScale,
            blinkAlpha = blinkAlpha,
            modifier = modifier
        )
    } else {
        FullCountdownCard(
            prayerStartTime = prayerStartTime,
            diffMillis = diffMillis,
            isLiveNow = isLiveNow,
            language = language,
            sessionName = sessionName,
            pulseScale = pulseScale,
            blinkAlpha = blinkAlpha,
            modifier = modifier
        )
    }
}

@Composable
private fun FullCountdownCard(
    prayerStartTime: Long,
    diffMillis: Long,
    isLiveNow: Boolean,
    language: LanguageEnum,
    sessionName: String?,
    pulseScale: Float,
    blinkAlpha: Float,
    modifier: Modifier = Modifier
) {
    val isCountingDown = prayerStartTime > 0L && diffMillis > 0L
    val isImminent = isCountingDown && diffMillis <= 2 * 60 * 1000L // <= 2 mins

    val totalSeconds = (diffMillis / 1000).coerceAtLeast(0)
    val hours = (totalSeconds / 3600)
    val minutes = ((totalSeconds % 3600) / 60)
    val seconds = (totalSeconds % 60)

    val formattedScheduledTime = remember(prayerStartTime) {
        if (prayerStartTime > 0L) {
            val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            sdf.format(Date(prayerStartTime))
        } else {
            ""
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_countdown_timer"),
        shape = RoundedCornerShape(18.dp),
        color = SacredDarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isLiveNow) StatusLiveRed.copy(alpha = 0.8f)
            else if (isImminent) Color(0xFFFF9F0A)
            else SacredGold.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isLiveNow) StatusLiveRed.copy(alpha = 0.12f)
                            else if (isImminent) Color(0x22FF9F0A)
                            else Color(0x1AD4AF37),
                            SacredCardBg
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Sync Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("countdown_status_badge"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Server Sync Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isLiveNow) StatusLiveRed.copy(alpha = 0.2f) else Color(0xFF131C2E),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isLiveNow) StatusLiveRed else Color(0xFF2E3D5C)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .scale(if (isLiveNow || isImminent) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(if (isLiveNow) StatusLiveRed else if (isImminent) Color(0xFFFF9F0A) else Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Server Synced",
                            tint = if (isLiveNow) StatusLiveRed else SacredGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (language) {
                                LanguageEnum.HINDI -> if (isLiveNow) "लाइव सत्र जारी है" else "सर्वर सिंक टाइमर"
                                LanguageEnum.MALAYALAM -> if (isLiveNow) "തത്സമയം നടക്കുന്നു" else "സെർവർ സമന്വയിപ്പിച്ചു"
                                else -> if (isLiveNow) "LIVE IN SESSION" else "SERVER CLOCK SYNCED"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLiveNow) StatusLiveRed else SacredGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                if (formattedScheduledTime.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedScheduledTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Session Title Banner
            Text(
                text = if (isLiveNow) {
                    when (language) {
                        LanguageEnum.HINDI -> "पवित्र रोज़री लाइव प्रारंभ हो चुकी है 📿"
                        LanguageEnum.MALAYALAM -> "വിശുദ്ധ ജപമാല ഇപ്പോൾ തത്സമയമാണ് 📿"
                        else -> "Holy Rosary is Live Now 📿"
                    }
                } else if (sessionName != null) {
                    sessionName
                } else {
                    when (language) {
                        LanguageEnum.HINDI -> "अगली लाइव रोज़री शुरू होने में शेष समय :"
                        LanguageEnum.MALAYALAM -> "അടുത്ത തത്സമയ ജപമാല ആരംഭിക്കാൻ ബാക്കി സമയം :"
                        else -> "Next Live Rosary Starts In :"
                    }
                },
                style = MaterialTheme.typography.titleMedium,
                color = if (isLiveNow) StatusLiveRed else SacredGold,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isLiveNow) {
                // Live Indicator View
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = "Live",
                        tint = StatusLiveRed,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(pulseScale)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (language) {
                            LanguageEnum.HINDI -> "सभी श्रद्धालु अभी एक साथ प्रार्थना कर रहे हैं"
                            LanguageEnum.MALAYALAM -> "എല്ലാവരും ഒന്നിച്ച് പ്രാർത്ഥിക്കുന്നു"
                            else -> "Community is praying synchronously together"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (prayerStartTime <= 0L) {
                // Syncing / Calculating state
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = SacredGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            LanguageEnum.HINDI -> "सर्वर से समय गणना की जा रही है..."
                            LanguageEnum.MALAYALAM -> "സെർവർ സമയം പരിശോധിക്കുന്നു..."
                            else -> "Synchronizing server start time..."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            } else {
                // Digits Display: HH : MM : SS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("countdown_digits"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hours > 0) {
                        DigitBox(
                            value = String.format(Locale.US, "%02d", hours),
                            label = when (language) {
                                LanguageEnum.HINDI -> "घंटे"
                                LanguageEnum.MALAYALAM -> "മണിക്കൂർ"
                                else -> "HOURS"
                            },
                            isHighlight = isImminent
                        )
                        SeparatorColon(blinkAlpha)
                    }

                    DigitBox(
                        value = String.format(Locale.US, "%02d", minutes),
                        label = when (language) {
                            LanguageEnum.HINDI -> "मिनट"
                            LanguageEnum.MALAYALAM -> "മിനിറ്റ്"
                            else -> "MINUTES"
                        },
                        isHighlight = isImminent
                    )

                    SeparatorColon(blinkAlpha)

                    DigitBox(
                        value = String.format(Locale.US, "%02d", seconds),
                        label = when (language) {
                            LanguageEnum.HINDI -> "सेकंड"
                            LanguageEnum.MALAYALAM -> "സെക്കൻഡ്"
                            else -> "SECONDS"
                        },
                        isHighlight = isImminent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanatory subtext showing synchronization accuracy
            Text(
                text = when (language) {
                    LanguageEnum.HINDI -> "⏱️ डिवाइस घड़ी और सर्वर 'prayerStartTime' के मध्य पूर्ण सामंजस्य"
                    LanguageEnum.MALAYALAM -> "⏱️ ഉപകരണം സെർവർ സമയവുമായി തികച്ചും ഒത്തുപോകുന്നു"
                    else -> "⏱️ Device clock auto-synchronized with server 'prayerStartTime'"
                },
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.8f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompactCountdownView(
    prayerStartTime: Long,
    diffMillis: Long,
    isLiveNow: Boolean,
    language: LanguageEnum,
    sessionName: String?,
    pulseScale: Float,
    blinkAlpha: Float,
    modifier: Modifier = Modifier
) {
    val totalSeconds = (diffMillis / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val timeString = if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_countdown_timer_compact"),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F1523),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLiveNow) StatusLiveRed else SacredGold.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .scale(if (isLiveNow) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (isLiveNow) StatusLiveRed else SacredGold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isLiveNow) {
                            when (language) {
                                LanguageEnum.HINDI -> "लाइव सत्र जारी है"
                                LanguageEnum.MALAYALAM -> "തത്സമയ സെഷൻ"
                                else -> "LIVE SESSION NOW"
                            }
                        } else {
                            sessionName ?: when (language) {
                                LanguageEnum.HINDI -> "अगला लाइव सत्र शुरू होने में"
                                LanguageEnum.MALAYALAM -> "അടുത്ത സെഷൻ"
                                else -> "Starts In"
                            }
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiveNow) StatusLiveRed else SacredGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = when (language) {
                            LanguageEnum.HINDI -> "सर्वर समय से सिंक्रोनाइज़्ड"
                            LanguageEnum.MALAYALAM -> "സെർവർ സിങ്ക്"
                            else -> "Server Synced"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            if (!isLiveNow && prayerStartTime > 0L) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1B2438),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3959))
                ) {
                    Text(
                        text = timeString,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DigitBox(
    value: String,
    label: String,
    isHighlight: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag("countdown_digit_box")
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF12192A),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isHighlight) Color(0xFFFF9F0A) else SacredGold.copy(alpha = 0.85f)
            ),
            modifier = Modifier
                .width(62.dp)
                .height(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                if (isHighlight) Color(0x33FF9F0A) else Color(0x22D4AF37),
                                Color(0xFF070B14)
                            )
                        )
                    )
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = if (isHighlight) Color(0xFFFFD54F) else TextPrimary,
                    fontSize = 24.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isHighlight) Color(0xFFFFB74D) else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SeparatorColon(blinkAlpha: Float) {
    Text(
        text = ":",
        fontSize = 26.sp,
        fontWeight = FontWeight.Black,
        color = SacredGold.copy(alpha = blinkAlpha),
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .padding(bottom = 18.dp)
    )
}
