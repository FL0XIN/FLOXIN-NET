package com.fl0xin.floxinnet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.fl0xin.floxinnet.ui.theme.*

@Composable
fun Page(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(DarkBg).padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        content()
    }
}

@Composable
fun PremiumCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier, shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = Surface), border = BorderStroke(1.dp, Border)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content) }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick() }, modifier.heightIn(min = 48.dp), shape = MaterialTheme.shapes.medium, colors = ButtonDefaults.buttonColors(containerColor = CyberBlue, contentColor = DarkBg)) { Text(text, style = MaterialTheme.typography.labelLarge) }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier.heightIn(min = 48.dp), shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, CyberBlue), colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberBlue)) { Text(text, style = MaterialTheme.typography.labelLarge) }
}

@Composable
fun StatusIndicator(running: Boolean) {
    Box(Modifier.size(8.dp).background(if (running) Success else TextMuted, CircleShape))
}

@Composable
fun RowScope.StatTile(label: String, value: String) {
    PremiumCard(Modifier.weight(1f)) { Text(value, style = MaterialTheme.typography.displayLarge, color = CyberBlue); Text(label, style = MaterialTheme.typography.labelMedium) }
}
