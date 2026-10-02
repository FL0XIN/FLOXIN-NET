package com.fl0xin.floxinnet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CyberBlue = Color(0xFF00D9FF)
val DarkBg = Color(0xFF0A0E1A)

@Composable
fun FloxinTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(primary = CyberBlue, background = DarkBg, surface = Color(0xFF111827)), content = content)
}
