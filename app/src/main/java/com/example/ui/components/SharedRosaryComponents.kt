package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageEnum
import com.example.model.MysteryType
import com.example.model.RosaryBeadStep
import com.example.ui.theme.*

@Composable
fun MysteryHeader(
    currentMystery: MysteryType,
    onMysteryChange: (MysteryType) -> Unit,
    language: LanguageEnum,
    isMuted: Boolean = false,
    onToggleMute: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = when (language) {
                    LanguageEnum.HINDI -> "सक्रिय भेद (MYSTERY)"
                    LanguageEnum.MALAYALAM -> "രഹസ്യം (MYSTERY)"
                    else -> "ACTIVE MYSTERY"
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }
        var expanded by remember { mutableStateOf(false) }
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SacredCardBg)
                    .border(1.dp, SacredCardBorder, RoundedCornerShape(10.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (language) {
                        LanguageEnum.HINDI -> currentMystery.hindiTitle
                        LanguageEnum.MALAYALAM -> currentMystery.malayalamTitle
                        else -> currentMystery.englishTitle
                    },
                    fontWeight = FontWeight.ExtraBold,
                    color = SacredGold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowDropDown, null, tint = SacredGold, modifier = Modifier.size(18.dp))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(SacredCardBg).border(1.dp, SacredCardBorder, RoundedCornerShape(8.dp))
            ) {
                MysteryType.values().forEach { mystery ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (language) {
                                    LanguageEnum.HINDI -> mystery.hindiTitle
                                    LanguageEnum.MALAYALAM -> mystery.malayalamTitle
                                    else -> mystery.englishTitle
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        onClick = {
                            onMysteryChange(mystery)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DecadeSelector(
    selectedDecade: Int,
    onDecadeSelected: ((Int) -> Unit)? = null,
    language: LanguageEnum,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when (language) {
                    LanguageEnum.HINDI -> "दशक :"
                    LanguageEnum.MALAYALAM -> "ദശകം:"
                    else -> "Decade:"
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            (1..5).forEach { decade ->
                val isSelected = decade == selectedDecade
                val baseModifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SacredGold else SacredCardBg)
                    .border(1.dp, if (isSelected) SacredGoldLight else SacredCardBorder, CircleShape)
                val finalModifier = if (onDecadeSelected != null) {
                    baseModifier.clickable { onDecadeSelected(decade) }
                } else baseModifier

                Box(
                    modifier = finalModifier,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$decade",
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }
        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Composable
fun MeditationThemeBox(currentStep: RosaryBeadStep?, currentMystery: MysteryType, language: LanguageEnum) {
    val currentDecade = currentStep?.decadeIndex ?: 1
    val mysteryList = remember(currentMystery) { com.example.data.RosaryPrayers.getMysteriesForType(currentMystery) }
    val activeMysteryInfo = mysteryList.getOrNull((currentDecade - 1).coerceIn(0, 4))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SacredCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SacredCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (language) {
                    LanguageEnum.HINDI -> activeMysteryInfo?.hindiMeditation ?: ""
                    LanguageEnum.MALAYALAM -> activeMysteryInfo?.malayalamMeditation ?: ""
                    else -> activeMysteryInfo?.englishMeditation ?: ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
