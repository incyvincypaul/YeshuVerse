package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun CandleItem(
    isLit: Boolean,
    flameScaleY: Float,
    flameScaleX: Float,
    flameAlpha: Float,
    auraScale: Float,
    flameSwayX: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier.height(72.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (isLit) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 26.dp)
                        .size(30.dp)
                        .graphicsLayer {
                            scaleX = auraScale
                            scaleY = auraScale
                            alpha = flameAlpha
                        }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF9F0A).copy(alpha = 0.40f),
                                    Color(0xFFFF6B00).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(bottom = 25.dp)
                        .size(width = 10.dp, height = 18.dp)
                        .graphicsLayer {
                            translationX = flameSwayX
                            scaleX = flameScaleX
                            scaleY = flameScaleY
                        },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStartPercent = 80, topEndPercent = 80, bottomStartPercent = 30, bottomEndPercent = 30))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFE5F55).copy(alpha = flameAlpha),
                                        Color(0xFFFF9F0A)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.65f)
                            .padding(bottom = 1.dp)
                            .clip(RoundedCornerShape(topStartPercent = 85, topEndPercent = 85, bottomStartPercent = 40, bottomEndPercent = 40))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFF275),
                                        Color(0xFFFFC300)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.35f)
                            .padding(bottom = 1.5.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.95f))
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(bottom = 23.dp)
                        .size(1.5.dp, 5.dp)
                        .background(Color(0xFF1C1D21))
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(bottom = 23.dp)
                        .size(1.5.dp, 4.dp)
                        .background(Color(0xFF5A6075))
                )
            }

            Box(
                modifier = Modifier
                    .size(12.dp, 24.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (isLit) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFD166),
                                    Color(0xFFFF9F0A),
                                    Color(0xFFD47A00)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF32394A),
                                    Color(0xFF202533),
                                    Color(0xFF141720)
                                )
                            )
                        }
                    )
                    .border(
                        width = 0.5.dp,
                        color = if (isLit) Color(0xFFFFF0B3).copy(alpha = 0.4f) else Color(0xFF4E5870).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}
