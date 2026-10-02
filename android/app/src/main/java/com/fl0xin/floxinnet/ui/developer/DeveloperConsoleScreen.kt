package com.fl0xin.floxinnet.ui.developer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fl0xin.floxinnet.ui.theme.CyberBlue

@Composable
fun DeveloperConsoleScreen(vm: DeveloperConsoleViewModel = viewModel()) {
    val lines by vm.lines.collectAsState()
    val fontSize by vm.fontSize.collectAsState()
    var command by remember { mutableStateOf("") }
    val scroll = rememberLazyListState()
    val clipboard = LocalClipboardManager.current
    LaunchedEffect(lines.size) { if (lines.isNotEmpty()) scroll.animateScrollToItem(lines.lastIndex) }
    Column(Modifier.fillMaxSize().background(Color.Black).padding(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Developer Console", color = CyberBlue, fontFamily = FontFamily.Monospace)
            Row { TextButton(onClick = vm::decreaseFont) { Text("A−") }; Text("$fontSize", color = Color.White, modifier = Modifier.padding(top = 12.dp)); TextButton(onClick = vm::increaseFont) { Text("A+") } }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = scroll) {
            items(lines) { line -> Text(line, color = Color(0xFFB7F7FF), fontFamily = FontFamily.Monospace, fontSize = fontSize.sp) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(onClick = { command = vm.previous() }) { Text("↑") }
            TextButton(onClick = { command = vm.next() }) { Text("↓") }
            TextButton(onClick = { clipboard.setText(AnnotatedString(vm.allText())) }) { Text("Copy") }
            BasicTextField(value = command, onValueChange = { command = it }, singleLine = true, textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = fontSize.sp), modifier = Modifier.weight(1f).background(Color(0xFF101820)).padding(12.dp), decorationBox = { inner -> Row { Text("$ ", color = CyberBlue, fontFamily = FontFamily.Monospace); inner() } })
            Button(onClick = { vm.execute(command); command = "" }) { Text("Run") }
        }
    }
}
