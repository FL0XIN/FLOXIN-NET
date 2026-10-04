package com.fl0xin.floxinnet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fl0xin.floxinnet.ui.theme.*

private val CardShape = RoundedCornerShape(20.dp)
private val ButtonShape = RoundedCornerShape(12.dp)

@Composable
fun FloxinText(text: String, style: TextStyle = TextStyle(fontSize = 15.sp, color = TextPrimary), modifier: Modifier = Modifier) = BasicText(text, style = style, modifier = modifier)

@Composable
fun Page(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(DarkBg).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FloxinText(title, TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
        content()
    }
}

/** Never nest LazyColumn inside Page's vertical scroll container. */
@Composable
fun ListPage(title: String, content: LazyListScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(DarkBg).padding(horizontal = 20.dp, vertical = 24.dp)) {
        FloxinText(title, TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
fun PremiumCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Box(modifier.clip(CardShape).background(Brush.linearGradient(listOf(Surface, Color(0xFF141B2D)))).border(BorderStroke(1.dp, Border), CardShape).padding(20.dp)) { Column(verticalArrangement = Arrangement.spacedBy(10.dp), content = content) }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.heightIn(min = 48.dp), shape = ButtonShape, colors = ButtonDefaults.buttonColors(containerColor = CyberBlue, contentColor = DarkBg), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)) {
        FloxinText(text, TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkBg))
    }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier.heightIn(min = 48.dp), shape = ButtonShape, border = BorderStroke(1.dp, CyberBlue), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)) {
        FloxinText(text, TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CyberBlue))
    }
}

@Composable
fun CompactAction(text: String, onClick: () -> Unit) { OutlinedButton(onClick = onClick, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) { FloxinText(text, TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CyberBlue)) } }

@Composable
fun CustomRadio(selected: Boolean, onClick: () -> Unit) { RadioButton(selected = selected, onClick = onClick) }

@Composable
fun CustomProgress(progress: Float) { Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Surface2)) { Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight().background(Brush.horizontalGradient(listOf(CyberBlue, Purple)), CircleShape)) } }

@Composable
fun CustomSwitch(checked: Boolean, onClick: () -> Unit) { Switch(checked = checked, onCheckedChange = { onClick() }) }

@Composable
fun StatusIndicator(running: Boolean) { Box(Modifier.size(10.dp).background(if (running) Success else TextMuted, CircleShape)) }

@Composable
fun RowScope.StatTile(label: String, value: String) { PremiumCard(Modifier.weight(1f)) { FloxinText(value, TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold, color = CyberBlue)); FloxinText(label, TextStyle(fontSize = 12.sp, color = TextMuted)) } }
