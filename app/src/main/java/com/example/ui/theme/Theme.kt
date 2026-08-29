package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = SacredGold,
  onPrimary = SacredBlack,
  primaryContainer = SacredGoldDark,
  onPrimaryContainer = SacredGoldLight,
  secondary = SacredBlueLight,
  onSecondary = SacredBlack,
  secondaryContainer = SacredBlueDark,
  onSecondaryContainer = SacredBlueLight,
  tertiary = SacredGoldLight,
  background = SacredBlack,
  onBackground = TextPrimary,
  surface = SacredDarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = SacredCardBg,
  onSurfaceVariant = TextSecondary,
  outline = SacredCardBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
