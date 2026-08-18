package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.RosaryPrayers
import com.example.model.LanguageEnum
import com.example.model.MysteryType
import com.example.model.RosaryBeadStep
import com.example.ui.theme.SacredBlue
import com.example.ui.theme.SacredBlueLight
import com.example.ui.theme.SacredCardBg
import com.example.ui.theme.SacredCardBorder
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.IconButton

@Composable
fun PrayerCard(
    currentStep: RosaryBeadStep?,
    mysteryType: MysteryType,
    language: LanguageEnum,
    modifier: Modifier = Modifier,
    onPreviousClick: (() -> Unit)? = null,
    onNextClick: (() -> Unit)? = null
) {
    var showPhonetic by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SacredCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row with Step Label and Language Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(SacredGold.copy(alpha = 0.2f), SacredBlue.copy(alpha = 0.2f))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = when (language) {
                            LanguageEnum.HINDI -> currentStep?.labelHindi ?: "प्रार्थना"
                            LanguageEnum.MALAYALAM -> currentStep?.labelMalayalam ?: "പ്രാർത്ഥന"
                            else -> currentStep?.labelEnglish ?: "Prayer"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = TextGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (language == LanguageEnum.HINDI) {
                    Surface(
                        onClick = { showPhonetic = !showPhonetic },
                        shape = RoundedCornerShape(12.dp),
                        color = if (showPhonetic) SacredGold.copy(alpha = 0.2f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (showPhonetic) SacredGold else SacredCardBorder),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (showPhonetic) "हिंदी देवनागरी" else "English Script",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (showPhonetic) TextGold else TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language",
                    tint = SacredBlueLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = language.nativeName,
                    style = MaterialTheme.typography.bodySmall,
                    color = SacredBlueLight,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mystery Meditation Banner (if associated with a mystery)
            if (currentStep?.mysteryIndex != null) {
                val mysteries = RosaryPrayers.getMysteriesForType(mysteryType)
                val mystery = mysteries.getOrNull(currentStep.mysteryIndex - 1)

                if (mystery != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(SacredBlue.copy(alpha = 0.25f), Color.Transparent)
                                )
                            )
                            .border(1.dp, SacredBlue.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = null,
                                    tint = SacredGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (language) {
                                        LanguageEnum.HINDI -> mystery.hindiTitle
                                        LanguageEnum.MALAYALAM -> mystery.malayalamTitle
                                        else -> mystery.englishTitle
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SacredGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (language) {
                                    LanguageEnum.HINDI -> mystery.hindiMeditation
                                    LanguageEnum.MALAYALAM -> mystery.malayalamMeditation
                                    else -> mystery.englishMeditation
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = (when (language) {
                                    LanguageEnum.HINDI -> "भेद का फल: "
                                    LanguageEnum.MALAYALAM -> "രഹസ്യത്തിന്റെ ഫലം: "
                                    else -> "Fruit of Mystery: "
                                }) + (when (language) {
                                    LanguageEnum.HINDI -> mystery.hindiFruit
                                    LanguageEnum.MALAYALAM -> mystery.malayalamFruit
                                    else -> mystery.englishFruit
                                }),
                                style = MaterialTheme.typography.labelSmall,
                                color = SacredGoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Main Prayer Text
            val prayerText = currentStep?.let { step ->
                if (showPhonetic && language == LanguageEnum.HINDI) {
                    RosaryPrayers.getRomanizedHindiPrayerText(step.prayerType)
                } else {
                    RosaryPrayers.getPrayerText(step.prayerType, language)
                }
            } ?: ""

            Text(
                text = prayerText,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Normal
            )

            // Dedicated Prev / Next Large Touch Targets
            if (onPreviousClick != null || onNextClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onPreviousClick != null) {
                        Surface(
                            onClick = onPreviousClick,
                            shape = RoundedCornerShape(14.dp),
                            color = SacredCardBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Bead",
                                    tint = SacredGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (language) {
                                        LanguageEnum.HINDI -> "पिछला मनका"
                                        LanguageEnum.MALAYALAM -> "മുമ്പത്തെ മണി"
                                        else -> "Previous Bead"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (onNextClick != null) {
                        Surface(
                            onClick = onNextClick,
                            shape = RoundedCornerShape(14.dp),
                            color = SacredGold,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (language) {
                                        LanguageEnum.HINDI -> "अगला मनका"
                                        LanguageEnum.MALAYALAM -> "അടുത്ത മണി"
                                        else -> "Next Bead"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Bead",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
