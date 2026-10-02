package com.fl0xin.floxinnet.ui.developer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.ui.components.*
import com.fl0xin.floxinnet.ui.theme.*

@Composable
fun DeveloperConsoleScreen(vm: DeveloperConsoleViewModel = viewModel()) {
    val lines by vm.lines.collectAsState(); val fontSize by vm.fontSize.collectAsState(); var command by remember { mutableStateOf("") }; val scroll = rememberLazyListState(); val clipboard = LocalClipboardManager.current
    LaunchedEffect(lines.size) { if (lines.isNotEmpty()) scroll.animateScrollToItem(lines.lastIndex) }
    Column(Modifier.fillMaxSize().background(DarkBg).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(stringResource(R.string.developer_title), color = CyberBlue, style = MaterialTheme.typography.titleLarge); Row { TextButton(onClick = vm::decreaseFont) { Text(stringResource(R.string.font_decrease)) }; Text("$fontSize", color = TextSecondary); TextButton(onClick = vm::increaseFont) { Text(stringResource(R.string.font_increase)) } } }
        PremiumCard(Modifier.weight(1f).fillMaxWidth()) { LazyColumn(Modifier.fillMaxSize(), state = scroll) { itemsIndexed(lines, key = { index, _ -> index }) { _, line -> Text(line, color = Color(0xFFB7F7FF), fontFamily = FontFamily.Monospace, fontSize = fontSize.sp) } } }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(onClick = { command = vm.previous() }) { Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = CyberBlue) }
            IconButton(onClick = { command = vm.next() }) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = CyberBlue) }
            SecondaryButton(stringResource(R.string.copy)) { clipboard.setText(AnnotatedString(vm.allText())) }
            BasicTextField(value = command, onValueChange = { command = it }, singleLine = true, textStyle = TextStyle(color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = fontSize.sp), modifier = Modifier.weight(1f).background(Surface2).padding(12.dp), decorationBox = { inner -> Row { Text("$ ", color = CyberBlue, fontFamily = FontFamily.Monospace); inner() } })
            PrimaryButton(stringResource(R.string.run)) { vm.execute(command); command = "" }
        }
    }
}
