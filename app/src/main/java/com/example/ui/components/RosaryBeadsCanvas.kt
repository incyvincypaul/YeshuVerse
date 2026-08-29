package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageEnum
import com.example.model.PrayerType
import com.example.model.RosaryBeadStep
import com.example.ui.theme.SacredBlueGlow
import com.example.ui.theme.SacredBlueLight
import com.example.ui.theme.SacredCardBg
import com.example.ui.theme.SacredCardBorder
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RosaryBeadsCanvas(
    steps: List<RosaryBeadStep>,
    currentStepIndex: Int,
    onBeadClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    language: LanguageEnum = LanguageEnum.ENGLISH,
    isPlaying: Boolean = false,
    onPlayPauseClick: (() -> Unit)? = null
) {
    val currentStep = steps.getOrNull(currentStepIndex)
    val currentDecade = currentStep?.decadeIndex ?: 1
    val currentBeadInDecade = currentStep?.beadInDecade ?: 0
    val currentPrayerType = currentStep?.prayerType ?: PrayerType.HAIL_MARY

    // Pulsating glow aura for active bead
    val infiniteTransition = rememberInfiniteTransition(label = "rosaryPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Gentle breathing pulse for the entire central sacred circle when recitation is active
    val centerBreathScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centerBreathScale"
    )

    val centerBreathAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centerBreathAlpha"
    )

    // Layout configuration
    val circleSize = 310.dp
    val centerCircleSize = 175.dp
    val radius = 105.dp // placing beads perfectly in the middle of outer ring

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(circleSize)
            .testTag("rosary_beads_circle_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer gentle devotional aura when playing
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .size(centerCircleSize + 28.dp)
                    .graphicsLayer {
                        scaleX = centerBreathScale
                        scaleY = centerBreathScale
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SacredGold.copy(alpha = centerBreathAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // 1. Central Sacred Badge Circle
        val isOurFather = currentPrayerType == PrayerType.OUR_FATHER
        Box(
            modifier = Modifier
                .size(centerCircleSize)
                .clip(CircleShape)
                .background(Color(0xFF0F1118))
                .border(2.dp, if (isPlaying || isOurFather) SacredGold else Color(0xFF1B2030), CircleShape)
                .clickable(enabled = onPlayPauseClick != null) {
                    onPlayPauseClick?.invoke()
                }
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Decade Index indicator
                Text(
                    text = when (language) {
                        LanguageEnum.HINDI -> if (currentDecade in 1..5) "दशक $currentDecade" else "प्रारंभिक प्रार्थनाएं"
                        LanguageEnum.MALAYALAM -> if (currentDecade in 1..5) "ദശകം $currentDecade" else "പ്രാരംഭ പ്രാർത്ഥനകൾ"
                        else -> if (currentDecade in 1..5) "Decade $currentDecade" else "Intro Prayers"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF9F0A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Active prayer short code or name
                val prayerShortTitle = when (language) {
                    LanguageEnum.HINDI -> when (currentPrayerType) {
                        PrayerType.SIGN_OF_THE_CROSS -> "क्रूस का चिन्ह"
                        PrayerType.APOSTLES_CREED -> "धर्मसार"
                        PrayerType.OUR_FATHER -> "हे हमारे पिता"
                        PrayerType.HAIL_MARY -> "प्रणाम मरीया"
                        PrayerType.GLORY_BE -> "पिता की महिमा"
                        PrayerType.FATIMA_PRAYER -> "ओ मेरे येसु"
                        PrayerType.HAIL_HOLY_QUEEN -> "हे पवित्र रानी"
                        PrayerType.MEMORARE -> "स्मरण प्रार्थना"
                        PrayerType.LITANY_OF_LORETO -> "मरियम की लितनियाँ"
                        PrayerType.CONCLUDING_PRAYER -> "समापन प्रार्थना"
                        PrayerType.INTRO_PRAYER -> "प्रारंभ"
                    }
                    LanguageEnum.MALAYALAM -> when (currentPrayerType) {
                        PrayerType.SIGN_OF_THE_CROSS -> "കുരിശടയാളം"
                        PrayerType.APOSTLES_CREED -> "വിശ്വാസപ്രമാണം"
                        PrayerType.OUR_FATHER -> "സ്വർഗ്ഗസ്ഥനായ പിതാവേ"
                        PrayerType.HAIL_MARY -> "നന്മ നിറഞ്ഞ മറിയമേ"
                        PrayerType.GLORY_BE -> "ത്രിത്വസ്തുതി"
                        PrayerType.FATIMA_PRAYER -> "ഓ എന്റെ ഈശോയേ"
                        PrayerType.HAIL_HOLY_QUEEN -> "പരിശുദ്ധ രാജ്ഞീ"
                        PrayerType.MEMORARE -> "ദയയുള്ള മാതാവേ"
                        PrayerType.LITANY_OF_LORETO -> "ലുത്തിനിയ"
                        PrayerType.CONCLUDING_PRAYER -> "സമാപന പ്രാർത്ഥന"
                        PrayerType.INTRO_PRAYER -> "പ്രാരംഭം"
                    }
                    else -> when (currentPrayerType) {
                        PrayerType.SIGN_OF_THE_CROSS -> "SIGN OF CROSS"
                        PrayerType.APOSTLES_CREED -> "APOSTLES' CREED"
                        PrayerType.OUR_FATHER -> "OUR FATHER"
                        PrayerType.HAIL_MARY -> "HAIL MARY"
                        PrayerType.GLORY_BE -> "GLORY BE"
                        PrayerType.FATIMA_PRAYER -> "FATIMA PRAYER"
                        PrayerType.HAIL_HOLY_QUEEN -> "HAIL HOLY QUEEN"
                        PrayerType.MEMORARE -> "THE MEMORARE"
                        PrayerType.LITANY_OF_LORETO -> "LITANY OF LORETO"
                        PrayerType.CONCLUDING_PRAYER -> "CONCLUDING PRAYER"
                        PrayerType.INTRO_PRAYER -> "INTRODUCTION"
                    }
                }

                Text(
                    text = prayerShortTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Single Elegant Central Play/Pause Orb Overlay
                if (onPlayPauseClick != null) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SacredGold, SacredGoldLight)
                                )
                            )
                            .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause Prayer" else "Start Prayer",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Active bead description pill
                val pillText = when (language) {
                    LanguageEnum.HINDI -> {
                        when (currentPrayerType) {
                            PrayerType.HAIL_MARY -> "मनका $currentBeadInDecade / 10"
                            PrayerType.OUR_FATHER -> "हे हमारे पिता"
                            PrayerType.GLORY_BE -> "पिता की महिमा"
                            PrayerType.FATIMA_PRAYER -> "ओ मेरे येसु"
                            PrayerType.SIGN_OF_THE_CROSS -> "क्रूस चिन्ह"
                            PrayerType.APOSTLES_CREED -> "धर्मसार"
                            PrayerType.INTRO_PRAYER -> "प्रारंभ"
                            else -> "पवित्र रानी"
                        }
                    }
                    LanguageEnum.MALAYALAM -> {
                        when (currentPrayerType) {
                            PrayerType.HAIL_MARY -> "മണി $currentBeadInDecade / 10"
                            PrayerType.OUR_FATHER -> "സ്വർഗ്ഗസ്ഥനായ പിതാവേ"
                            PrayerType.GLORY_BE -> "ത്രിത്വസ്തുതി"
                            PrayerType.FATIMA_PRAYER -> "ഓ എന്റെ ഈശോയേ"
                            PrayerType.SIGN_OF_THE_CROSS -> "കുരിശടയാളം"
                            PrayerType.APOSTLES_CREED -> "വിശ്വാസപ്രമാണം"
                            PrayerType.INTRO_PRAYER -> "പ്രാരംഭം"
                            else -> "പരിശുദ്ധ രാജ്ഞീ"
                        }
                    }
                    else -> {
                        when (currentPrayerType) {
                            PrayerType.HAIL_MARY -> "Bead $currentBeadInDecade / 10"
                            PrayerType.OUR_FATHER -> "Our Father"
                            PrayerType.GLORY_BE -> "Glory Be"
                            PrayerType.FATIMA_PRAYER -> "Fatima Prayer"
                            PrayerType.SIGN_OF_THE_CROSS -> "Sign of Cross"
                            PrayerType.APOSTLES_CREED -> "Apostles' Creed"
                            PrayerType.INTRO_PRAYER -> "Introduction"
                            else -> "Hail Holy Queen"
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFFF9F0A), RoundedCornerShape(20.dp))
                        .background(Color(0xFFFF9F0A).copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = pillText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF9F0A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // 2. Render 12 Clock-aligned Nodes Around the Circle
        // 0 to 11 (0=G, 1..5=Beads 1..5, 6=P, 7..11=Beads 6..10)
        for (i in 0 until 12) {
            val angleDegrees = when (i) {
                0 -> -90f   // Hour 12: G (Gloria)
                1 -> -60f   // Hour 1: Bead 1
                2 -> -30f   // Hour 2: Bead 2
                3 -> 0f     // Hour 3: Bead 3
                4 -> 30f    // Hour 4: Bead 4
                5 -> 60f    // Hour 5: Bead 5
                6 -> 90f    // Hour 6: P (Pater / Our Father)
                7 -> 120f   // Hour 7: Bead 6
                8 -> 150f   // Hour 8: Bead 7
                9 -> 180f   // Hour 9: Bead 8
                10 -> 210f  // Hour 10: Bead 9
                11 -> 240f  // Hour 11: Bead 10
                else -> 0f
            }

            val angleRad = Math.toRadians(angleDegrees.toDouble())
            val xOffset = (radius.value * cos(angleRad)).dp
            val yOffset = (radius.value * sin(angleRad)).dp

            // Determine if active, completed, or upcoming
            val nodeState = when {
                // Out of range (outro completed)
                currentDecade > 5 -> BeadState.COMPLETED
                currentDecade == 0 -> {
                    // Intro Prayers (steps 0..6)
                    when (i) {
                        6 -> if (currentStepIndex == 2) BeadState.ACTIVE else if (currentStepIndex > 2) BeadState.COMPLETED else BeadState.UPCOMING
                        1 -> if (currentStepIndex == 3) BeadState.ACTIVE else if (currentStepIndex > 3) BeadState.COMPLETED else BeadState.UPCOMING
                        2 -> if (currentStepIndex == 4) BeadState.ACTIVE else if (currentStepIndex > 4) BeadState.COMPLETED else BeadState.UPCOMING
                        3 -> if (currentStepIndex == 5) BeadState.ACTIVE else if (currentStepIndex > 5) BeadState.COMPLETED else BeadState.UPCOMING
                        0 -> if (currentStepIndex == 6) BeadState.ACTIVE else BeadState.UPCOMING
                        else -> BeadState.UPCOMING
                    }
                }

                // Viewing current decade (1..5)
                else -> {
                    when (i) {
                        6 -> { // Pater Noster (P)
                            if (currentBeadInDecade == 0 && currentPrayerType == PrayerType.OUR_FATHER) {
                                BeadState.ACTIVE
                            } else if (currentBeadInDecade > 0 || currentPrayerType == PrayerType.GLORY_BE || currentPrayerType == PrayerType.FATIMA_PRAYER) {
                                BeadState.COMPLETED
                            } else {
                                BeadState.UPCOMING
                            }
                        }
                        0 -> { // Glory Be / Fatima (G)
                            if (currentBeadInDecade >= 11 || currentPrayerType == PrayerType.GLORY_BE || currentPrayerType == PrayerType.FATIMA_PRAYER) {
                                BeadState.ACTIVE
                            } else {
                                BeadState.UPCOMING
                            }
                        }
                        else -> { // Beads 1 to 10
                            val beadNumber = if (i in 1..5) i else i - 1
                            if (currentBeadInDecade == beadNumber && currentPrayerType == PrayerType.HAIL_MARY) {
                                BeadState.ACTIVE
                            } else if (currentBeadInDecade > beadNumber || currentPrayerType == PrayerType.GLORY_BE || currentPrayerType == PrayerType.FATIMA_PRAYER) {
                                BeadState.COMPLETED
                            } else {
                                BeadState.UPCOMING
                            }
                        }
                    }
                }
            }

            // Map node click to step index jump
            val targetStepIndex = if (currentDecade == 0) {
                when (i) {
                    6 -> 2 // Intro Our Father
                    1 -> 3 // Hail Mary 1
                    2 -> 4 // Hail Mary 2
                    3 -> 5 // Hail Mary 3
                    0 -> 6 // Intro Glory Be
                    else -> steps.firstOrNull { it.decadeIndex == 1 && it.beadInDecade == (if (i in 1..5) i else i - 1) }?.stepIndex
                }
            } else {
                val activeDecade = if (currentDecade in 1..5) currentDecade else 1
                when (i) {
                    6 -> steps.firstOrNull { it.decadeIndex == activeDecade && it.beadInDecade == 0 }?.stepIndex
                    0 -> steps.firstOrNull { it.decadeIndex == activeDecade && it.beadInDecade == 11 }?.stepIndex
                    else -> {
                        val beadNumber = if (i in 1..5) i else i - 1
                        steps.firstOrNull { it.decadeIndex == activeDecade && it.beadInDecade == beadNumber }?.stepIndex
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = xOffset, y = yOffset),
                contentAlignment = Alignment.Center
            ) {
                if (nodeState == BeadState.ACTIVE) {
                    // Pulsating golden-orange glow aura
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                            .clip(CircleShape)
                            .background(Color(0xFFFF9F0A).copy(alpha = 0.22f))
                    )
                }

                // Node content
                val nodeSize = if (nodeState == BeadState.ACTIVE) 38.dp else 32.dp
                val nodeBgColor = when (nodeState) {
                    BeadState.ACTIVE -> Color(0xFFFF9F0A)
                    BeadState.COMPLETED -> Color(0xFFFF9F0A).copy(alpha = 0.12f)
                    BeadState.UPCOMING -> Color(0xFF141722)
                }
                val nodeBorderColor = when (nodeState) {
                    BeadState.ACTIVE -> Color(0xFFFFD166)
                    BeadState.COMPLETED -> Color(0xFFFF9F0A).copy(alpha = 0.65f)
                    BeadState.UPCOMING -> Color(0xFF232A3E)
                }
                val nodeTextColor = when (nodeState) {
                    BeadState.ACTIVE -> Color.Black
                    BeadState.COMPLETED -> Color(0xFFFF9F0A)
                    BeadState.UPCOMING -> TextSecondary
                }

                Box(
                    modifier = Modifier
                        .size(nodeSize)
                        .clip(CircleShape)
                        .background(nodeBgColor)
                        .border(1.5.dp, nodeBorderColor, CircleShape)
                        .clickable {
                            targetStepIndex?.let { onBeadClick(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val displayText = when (i) {
                        0 -> "G"
                        6 -> "P"
                        else -> {
                            val beadNumber = if (i in 1..5) i else i - 1
                            if (currentPrayerType == PrayerType.OUR_FATHER) "" else "$beadNumber"
                        }
                    }
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = nodeTextColor,
                        fontSize = if (nodeState == BeadState.ACTIVE) 13.sp else 11.sp
                    )
                }
            }
        }
    }
}

enum class BeadState {
    UPCOMING,
    ACTIVE,
    COMPLETED
}
