package com.fl0xin.floxinnet.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val FloxinColors = darkColorScheme(
    primary = CyberBlue,
    onPrimary = DarkBg,
    secondary = Purple,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = Danger
)

private val FloxinTypography = Typography(
    displayLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    bodyLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, color = TextPrimary),
    bodyMedium = TextStyle(fontSize = 14.sp, color = TextSecondary),
    labelMedium = TextStyle(fontSize = 13.sp, color = TextMuted),
    labelLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
)

@Composable
fun FloxinTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = FloxinColors, typography = FloxinTypography, shapes = FloxinShapes, content = content)
}
