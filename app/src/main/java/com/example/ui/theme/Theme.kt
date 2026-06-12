package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DetectiveGold,
    onPrimary = ObsidianDark,
    primaryContainer = GraphiteCard,
    onPrimaryContainer = DetectiveGold,
    secondary = DetectiveBlue,
    onSecondary = ObsidianDark,
    tertiary = CrimsonAccent,
    onTertiary = TextPrimary,
    background = ObsidianDark,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = GraphiteCard,
    onSurfaceVariant = TextSecondary,
    secondaryContainer = GraphiteCard,
    onSecondaryContainer = DetectiveBlue,
    outline = GridLine
  )

private val LightColorScheme = DarkColorScheme // Force dark theme for atmospheric detective vibe


@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme


  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
